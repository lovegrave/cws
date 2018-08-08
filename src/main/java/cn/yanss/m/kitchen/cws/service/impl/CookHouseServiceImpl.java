package cn.yanss.m.kitchen.cws.service.impl;

import cn.yanss.m.kitchen.cws.anntate.ServiceLimit;
import cn.yanss.m.kitchen.cws.api.OrderClient;
import cn.yanss.m.kitchen.cws.cache.EhCacheServiceImpl;
import cn.yanss.m.kitchen.cws.cache.RedisService;
import cn.yanss.m.kitchen.cws.constant.OrderStatus;
import cn.yanss.m.kitchen.cws.disruptor.NotifyServiceImpl;
import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.entity.request.OrderRequest;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import cn.yanss.m.kitchen.cws.service.CookHouseService;
import cn.yanss.m.kitchen.cws.service.DispatcherService;
import cn.yanss.m.kitchen.cws.service.impl.thread.OrderModifyTask;
import cn.yanss.m.kitchen.cws.service.impl.thread.pool.ThreadPool;
import cn.yanss.m.kitchen.cws.utils.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.returnModel.ReturnModel;
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
public class CookHouseServiceImpl implements CookHouseService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private EhCacheServiceImpl ehCacheService;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private NotifyServiceImpl notifyService;
    @Autowired
    private DispatcherService dispatcherService;


    /**
     * 手动查询该店铺下特定状态的订单集合
     * @param orderRequest
     * @return
     */
    @Override
    @ServiceLimit
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
        String key = orderRequest.getStoreId()+""+orderRequest.getStatus();
        Set<String> keys = redisService.zrange(key,0,-1);
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

        if(!orderId.startsWith("PT")){
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
        OrderResponse orderResponse = ehCacheService.getValue(orderId,OrderResponse.class);
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
        if(status == 1){
            ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest();
            modifyOrderRequest.setOrderId(orderId);
            modifyOrderRequest.setOrderStatus(2);
            modifyOrderRequest.setSendStatus(2);
            ThreadPool.pool.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
            orderResponse.setTotalStatus(OrderStatus.INDEVELOPMENT);
            orderResponse.setOrderStatus(OrderStatus.INDEVELOPMENT);
            orderResponse.setSendStatus(OrderStatus.INDEVELOPMENT);
        }else if(status == 5 || status == 6){
            orderResponse.setSendStatus(OrderStatus.INDEVELOPMENT);
            orderResponse.setTotalStatus(OrderStatus.INDEVELOPMENT);
        }else{
            return new ReturnModel(500,"该操作无效");
        }
        notifyService.sendNotify(orderResponse);
        redisService.lpush(OrderStatus.FLOW+OrderStatus.ACCOUNT_PAID,orderResponse.getOrderId(),7200);
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
        OrderResponse orderResponse = ehCacheService.getValue(orderId,OrderResponse.class);
        if(null == orderResponse){
            return new ReturnModel(500,"该订单不存在或已过期");
        }
        Integer totalStatus = orderResponse.getTotalStatus();
        if(totalStatus == OrderStatus.DIS_COMPLETE){
            return new ReturnModel(500,"订单已完成,不允许取消,特殊情况请联系后台");
        }
        if(totalStatus == 1){
            orderResponse.setCancelCode(3);
            orderResponse.setTotalStatus(6);
            notifyService.sendNotify(orderResponse);
            return new ReturnModel();
        }
        ReturnModel cancelModel = dispatcherService.cancelOrder(orderId, 3);
        if (cancelModel.getCode() == 200) {
            ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest();
            modifyOrderRequest.setOrderId(orderId);
            modifyOrderRequest.setExceptionStatus(100);
            modifyOrderRequest.setExceptionRemark("厨房端主动取消");
            ThreadPool.pool.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
            orderResponse.setTotalStatus(6);
            orderResponse.setCancelCode(3);
            orderResponse.setExceptionStatus(100);
            orderResponse.setExceptionRemark("厨房端主动取消");
            notifyService.sendNotify(orderResponse);
            return cancelModel;
        }else{
            if(totalStatus > 2){
                return new ReturnModel(500,"该订单第三方配送公司不允许取消");
            }
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
        if("1".equalsIgnoreCase(redisService.getString(key))){
            String value = redisService.watch(key);
            if("Ok".equalsIgnoreCase(value)){
                Transaction t = redisService.multi();
                redisService.setString(key,"4");
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
        return future.get();
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
        modifyOrderRequest.setExceptionStatus(199);
        String remark = modifyOrderRequest.getExceptionRemark()+"厨房申请退款";
        modifyOrderRequest.setExceptionRemark(remark);
        orderResponse.setTotalStatus(99);
        orderResponse.setExceptionStatus(199);
        orderResponse.setExceptionRemark(remark);
        notifyService.sendNotify(orderResponse);
        redisService.lpush(OrderStatus.REFUND+orderResponse.getStoreId(),MapperUtils.obj2jsonIgnoreNull(orderResponse),90000);
        return ThreadPool.pool.submit(new OrderModifyTask(modifyOrderRequest,orderClient)).get();
    }
}
