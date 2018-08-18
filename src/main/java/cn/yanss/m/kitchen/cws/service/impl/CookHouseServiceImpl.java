package cn.yanss.m.kitchen.cws.service.impl;

import cn.yanss.m.kitchen.cws.api.OrderClient;
import cn.yanss.m.kitchen.cws.api.ProductClient;
import cn.yanss.m.kitchen.cws.cache.EhCacheServiceImpl;
import cn.yanss.m.kitchen.cws.cache.RedisService;
import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.constant.OrderStatus;
import cn.yanss.m.kitchen.cws.disruptor.NotifyServiceImpl;
import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.entity.request.OrderRequest;
import cn.yanss.m.kitchen.cws.entity.request.StoreRequest;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import cn.yanss.m.kitchen.cws.service.CookHouseService;
import cn.yanss.m.kitchen.cws.service.DispatcherService;
import cn.yanss.m.kitchen.cws.service.impl.thread.OrderModifyTask;
import cn.yanss.m.kitchen.cws.service.impl.thread.pool.ThreadPool;
import cn.yanss.m.kitchen.cws.utils.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CookHouseServiceImpl implements CookHouseService {

    private final RedisService redisService;
    private final EhCacheServiceImpl ehCacheService;
    private final OrderClient orderClient;
    private final ProductClient productClient;
    private final NotifyServiceImpl notifyService;
    private final DispatcherService dispatcherService;

    @Autowired
    public CookHouseServiceImpl(RedisService redisService, EhCacheServiceImpl ehCacheService, OrderClient orderClient, NotifyServiceImpl notifyService, DispatcherService dispatcherService,ProductClient productClient) {
        this.redisService = redisService;
        this.ehCacheService = ehCacheService;
        this.orderClient = orderClient;
        this.notifyService = notifyService;
        this.dispatcherService = dispatcherService;
        this.productClient = productClient;
    }

    /**
     * 手动查询该店铺下特定状态的订单集合,采用分页查询
     * @param orderRequest
     * @return
     */
    @Override
    public ReturnModel findOrderList(OrderRequest orderRequest) {
        Integer status = orderRequest.getStatus();
        /**
         * 订单申请退款
         */
        if(status == OrderStatus.REFUND_ORDERS){
            return new ReturnModel(redisService.lpList(OrderStatus.REFUND+orderRequest.getStoreId()));
        }
        /**
         * 订单已完成
         */
        if(status == OrderStatus.DIS_COMPLETE){
            return new ReturnModel(redisService.lpList(OrderStatus.COMPLETE+orderRequest.getStoreId()));
        }
        /**
         * 其它进行时配送状态订单
         */
        String key = orderRequest.getStoreId()+""+orderRequest.getStatus();
        Integer start = (orderRequest.getCurrentPage()-1)*orderRequest.getPageSize();
        Integer end = orderRequest.getCurrentPage()*orderRequest.getPageSize()-1;
        Set<String> keys = redisService.zrange(key,start,end);
        List<String> orderIds = keys.stream().collect(Collectors.toList());
        List<Object> list = ehCacheService.getList(orderIds);
        return new ReturnModel(list);
    }

    /**
     * 根据订单号或者订单的取货号查询订单详情
     * @param orderId
     * @param storeId
     * @return
     */
    @Override
    public ReturnModel findOrderDetail(String orderId, Integer storeId) {
        if(StringUtils.isEmpty(orderId)){
            return new ReturnModel();
        }
        /**
         * 判断orderId是订单号还是取货号
         */
        if('P' == orderId.charAt(0)){
            orderId = redisService.getMapString(OrderStatus.ORDER_PICK+storeId,orderId);
        }
        if(StringUtils.isEmpty(orderId)){
            return new ReturnModel();
        }
        Object o = ehCacheService.getObj(orderId);
        if(null == o){
            return orderClient.detail(orderId);
        }else{
            return new ReturnModel(o);
        }

    }

    /**
     * 接单接口,后期看情况简化删除
     * @param orderId
     * @return
     */
    @Override
    public ReturnModel opt(String orderId) {
        OrderResponse orderResponse = (OrderResponse) ehCacheService.getObj(orderId);
        if(null == orderResponse){
            ReturnModel returnModel = orderClient.detail(orderId);
            orderResponse = null != returnModel.getData()? MapperUtils.obj2pojo(returnModel.getData(),OrderResponse.class):null;

        }
        if(null == orderResponse){
            return new ReturnModel(500,OrderStatus.ORDER_NOT_EXISTS);
        }else{
            orderResponse.setOrderPick(orderResponse.getOrderNo());
        }
        Integer status = orderResponse.getTotalStatus();
        /**
         * 正常接单接口
         */
        if(status == 1){
            ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest();
            modifyOrderRequest.setOrderId(orderId);
            modifyOrderRequest.setOrderStatus(2);
            modifyOrderRequest.setSendStatus(2);
            ThreadPool.pool.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
            orderResponse.setTotalStatus(OrderStatus.INDEVELOPMENT);
            orderResponse.setOrderStatus(OrderStatus.INDEVELOPMENT);
            orderResponse.setSendStatus(OrderStatus.INDEVELOPMENT);
        /**
         * 异常订单接单接口,并不建议在异常状态为5的时候使用
         */
        }else if(status == 5 || status == 6){
            orderResponse.setSendStatus(OrderStatus.INDEVELOPMENT);
            orderResponse.setTotalStatus(OrderStatus.INDEVELOPMENT);
        }else{
            return new ReturnModel(500,"该操作无效");
        }
        notifyService.sendNotify(orderResponse);
        if(orderResponse.getAppType() != 3){
            redisService.lpush(OrderStatus.FLOW+OrderStatus.ACCOUNT_PAID,orderResponse.getOrderId(),7200);
        }
        return new ReturnModel();
    }

    /**
     * 将订单置为厨房端异常
     * @param orderId
     * @return ReturnModel
     * @throws Exception
     */
    @Override
    public ReturnModel orderException(String orderId) throws Exception {
        /**
         * 能强转是由于存入数据时采用订单对象
         */
        OrderResponse orderResponse = (OrderResponse) ehCacheService.getObj(orderId);
        if(null == orderResponse){
            return new ReturnModel(500,"该订单不存在或已过期");
        }
        Integer totalStatus = orderResponse.getTotalStatus();
        /**
         * 在订单已完成的情况下不能手动报异常
         */
        if(totalStatus == OrderStatus.DIS_COMPLETE){
            return new ReturnModel(500,"订单已完成,不允许取消,特殊情况请联系后台");
        }
        /**
         * 当订单状态为1时,直接转为异常状态
         */
        if(totalStatus == 1){
            orderResponse.setCancelCode(3);
            orderResponse.setTotalStatus(6);
            ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest();
            modifyOrderRequest.setOrderId(orderId);
            modifyOrderRequest.setExceptionStatus(100);
            modifyOrderRequest.setExceptionRemark("厨房取消");
            ThreadPool.pool.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
            redisService.lrem(OrderStatus.FLOW+1,orderId);
            notifyService.sendNotify(orderResponse);
            return new ReturnModel();
        }
        /**
         * 当订单状态不为1时，尝试取消第三方订单
         */
        ReturnModel cancelModel = dispatcherService.cancelOrder(orderResponse);
        if (cancelModel.getCode() == 200) {
            ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest();
            modifyOrderRequest.setOrderId(orderId);
            modifyOrderRequest.setExceptionStatus(100);
            modifyOrderRequest.setExceptionRemark("厨房端主动取消");
            /**
             * 取消订单成功,则把订单通知order模块
             */
            ThreadPool.pool.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
            /**
             * 将订单置为厨房端异常,可恢复,也可以直接申请退款
             */
            orderResponse.setTotalStatus(6);
            orderResponse.setCancelCode(3);
            orderResponse.setExceptionStatus(100);
            orderResponse.setExceptionRemark("厨房端主动取消");
            /**
             * 将订单存入缓存并,通知给厨房
             */
            notifyService.sendNotify(orderResponse);
            return cancelModel;
        }else{
            /**
             * 如果订单状态大于2,则判定取消失败
             */
            if(totalStatus > 2){
                return new ReturnModel(500,"该订单第三方配送公司不允许取消");
            }
            /**
             * 当订单状态为2时,判断订单的取消状态,如果不为0,则通知挂起
             */
            if(orderResponse.getCancelCode() != 0){
                return new ReturnModel(500,"该订单已经被挂起,请静心等待");
            }
            orderResponse.setCancelCode(1);
            redisService.setString(OrderStatus.CANCEL+orderId,"1");
            notifyService.sendNotify(orderResponse);
            return new ReturnModel(500,"订单已被挂起");
        }
    }

    /**
     * 撤销取消订单
     * @param orderId
     * @return
     */
    @Override
    public ReturnModel abandonCancel(String orderId) {
        String key = OrderStatus.CANCEL+orderId;
        /**
         * 判断挂起订单的取消状态是否为1,否则直接判定为取消失败
         */
        if("1".equalsIgnoreCase(redisService.getString(key))){
            /**
             * 监控该订单
             */
            String value = redisService.watch(key);
            if("Ok".equalsIgnoreCase(value)){
                /**
                 * 拉起redis事物
                 */
                Transaction t = redisService.multi();
                redisService.setString(key,"4");
                /**
                 * 判定redis事物是否执行成功
                 */
                if(null == t.exec()){
                    return new ReturnModel(500,"撤销失败");
                }else{
                    redisService.remove(key);
                    OrderResponse orderResponse = ehCacheService.getValue(orderId,OrderResponse.class);
                    orderResponse.setCancelCode(4);
                    notifyService.sendNotify(orderResponse);
                    return new ReturnModel();
                }
            }
        }
        return new ReturnModel(500,"撤销失败");
    }

    /**
     * 将订单置为已完成
     * @param orderId
     * @return
     * @throws JsonProcessingException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public ReturnModel orderFinish(String orderId) throws JsonProcessingException, ExecutionException, InterruptedException {
        OrderResponse orderResponse = ehCacheService.getValue(orderId,OrderResponse.class);
        if(null == orderResponse){
            ReturnModel returnModel = orderClient.detail(orderId);
            orderResponse = null == returnModel.getData() ? null : MapperUtils.obj2pojo(returnModel.getData(),OrderResponse.class);
        }
        if(null == orderResponse){
            return new ReturnModel(500,OrderStatus.ORDER_NOT_EXISTS);
        }else{
            orderResponse.setOrderPick(orderResponse.getOrderNo());
        }
        ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest();
        modifyOrderRequest.setOrderId(orderId);
        modifyOrderRequest.setSendStatus(7);
        modifyOrderRequest.setOrderStatus(4);
        modifyOrderRequest.setExceptionStatus(0);
        orderResponse.setTotalStatus(4);
        orderResponse.setSendStatus(7);
        orderResponse.setOrderStatus(4);
        orderResponse.setExceptionStatus(0);
        redisService.setObject(orderId,orderResponse,90000);
        redisService.lpush(OrderStatus.COMPLETE+orderResponse.getStoreId(),MapperUtils.obj2jsonIgnoreNull(orderResponse),90000);
        notifyService.sendNotify(orderResponse);
        Future<ReturnModel> future = ThreadPool.pool.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
        try {
            return future.get();
        } catch (Exception e) {
            log.error("orderFinish-->"+orderId,e.getMessage());
            return new ReturnModel(500,"修改失败");
        }
    }

    @Override
    public ReturnModel applicationForRefund(String orderId) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        OrderResponse orderResponse = ehCacheService.getValue(orderId,OrderResponse.class);
        if(null == orderResponse){
            ReturnModel returnModel = orderClient.detail(orderId);
            orderResponse = MapperUtils.obj2pojo(returnModel.getData(),OrderResponse.class);
        }
        if(null ==orderResponse){
            return new ReturnModel(500,OrderStatus.ORDER_NOT_EXISTS);
        }
        ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest();
        modifyOrderRequest.setOrderId(orderId);
        modifyOrderRequest.setExceptionStatus(200);
        String remark = modifyOrderRequest.getExceptionRemark()+"厨房申请退款";
        modifyOrderRequest.setExceptionRemark(remark);
        orderResponse.setTotalStatus(7);
        orderResponse.setExceptionStatus(200);
        orderResponse.setExceptionRemark(remark);
        notifyService.sendNotify(orderResponse);
        redisService.lpush(OrderStatus.REFUND+orderResponse.getStoreId(),MapperUtils.obj2jsonIgnoreNull(orderResponse),90000);
        Future<ReturnModel> future = ThreadPool.pool.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
        try {
            return future.get();
        } catch (Exception e) {
            log.error("applicationForRefund-->"+orderId,e.getMessage());
            return new ReturnModel(500,"修改失败");
        }
    }

    @Override
    public ReturnModel findStoreId(StoreRequest storeRequest) {
        if (storeRequest.getStoreId() == null || storeRequest.getStoreId() == 0) {
            return productClient.findStoreList();
        } else {
            return null;
        }
    }

    private void pullMessage(){

    }
}
