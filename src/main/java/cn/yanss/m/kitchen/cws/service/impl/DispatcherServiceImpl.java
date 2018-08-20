package cn.yanss.m.kitchen.cws.service.impl;

import cn.yanss.m.kitchen.cws.api.DispatcherClient;
import cn.yanss.m.kitchen.cws.api.OrderClient;
import cn.yanss.m.kitchen.cws.cache.EhCacheServiceImpl;
import cn.yanss.m.kitchen.cws.cache.RedisService;
import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.constant.OrderStatus;
import cn.yanss.m.kitchen.cws.disruptor.NotifyServiceImpl;
import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import cn.yanss.m.kitchen.cws.service.DispatcherService;
import cn.yanss.m.kitchen.cws.service.impl.thread.OrderModifyTask;
import cn.yanss.m.kitchen.cws.service.impl.thread.pool.ThreadPool;
import cn.yanss.m.kitchen.cws.utils.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@Log4j2
public class DispatcherServiceImpl implements DispatcherService {

    private final OrderClient orderClient;
    private final DispatcherClient dispatcherClient;
    private final RedisService redisService;
    private final EhCacheServiceImpl ehCacheService;
    private final NotifyServiceImpl notifyService;
    private final ExecutorService executorService;

    @Autowired
    public DispatcherServiceImpl(OrderClient orderClient, DispatcherClient dispatcherClient, RedisService redisService, EhCacheServiceImpl ehCacheService, NotifyServiceImpl notifyService,ExecutorService executorService) {
        this.orderClient = orderClient;
        this.dispatcherClient = dispatcherClient;
        this.redisService = redisService;
        this.ehCacheService = ehCacheService;
        this.notifyService = notifyService;
        this.executorService = executorService;
    }
    @Override
    public ReturnModel gainOrder() {
        List<String> orderIds = redisService.lpList(OrderStatus.FLOW+OrderStatus.ACCOUNT_PAID);
        List orderResponses = ehCacheService.getList(orderIds);
        return new ReturnModel(orderResponses);
    }

    @Override
    public ReturnModel callback(ModifyOrderRequest modifyOrderRequest) throws Exception {

        return new ReturnModel();
    }

    /**
     * 删除订单
     * @param orderId
     * @param cancelCode
     * @return
     * @throws IOException
     */
    @Override
    public ReturnModel cancelOrder(String orderId, Integer cancelCode) throws IOException {
        OrderResponse orderResponse = ehCacheService.getValue(orderId, OrderResponse.class);
        if (null == orderResponse) {
            ReturnModel returnModel = orderClient.detail(orderId);
            ObjectMapper objectMapper = new ObjectMapper();
            orderResponse = objectMapper.readValue(objectMapper.writeValueAsString(returnModel.getData()),OrderResponse.class);
        }
        if (null == orderResponse) {
            return new ReturnModel(600, "订单不存在");
        }
        if(null != orderResponse.getExceptionStatus() && orderResponse.getExceptionStatus() == OrderStatus.ERROR_STATUS){
            return new ReturnModel();
        }
        if ("厨房端主动取消".equals(orderResponse.getExceptionRemark())) {
            return new ReturnModel();
        }
        if (orderResponse.getOrderStatus() == OrderStatus.ACCOUNT_PAID) {
            return new ReturnModel();
        } else if (null != orderResponse.getSendStatus() && 0 < orderResponse.getSendStatus() && orderResponse.getSendStatus() < 3) {
            orderResponse.setCancelCode(cancelCode);
            return dispatcherClient.cancelCode(orderResponse);
        } else {
            return new ReturnModel(600, "订单在制作或已发往第三方公司,不允许取消");
        }
    }

    /**
     * 新版取消订单
     * @param orderResponse
     * @return
     */
    @Override
    public ReturnModel cancelOrder(OrderResponse orderResponse) {
        if(null == orderResponse){
            return new ReturnModel(500,"订单不存在");
        }
        Integer status = orderResponse.getTotalStatus();
        if(status == OrderStatus.ACCOUNT_PAID || status == OrderStatus.DIS_COMPLETE || status == OrderStatus.KITCHEN_ERROR || status == OrderStatus.REFUND_ORDERS){
            return new ReturnModel();
        }
        return dispatcherClient.cancelCode(orderResponse);
    }

    /**
     * 订单运行时异常,该异常是厨房端需要注意的异常，不一定是必须要处理的异常
     * 修改的属性：异常编码,异常信息,报异常时间
     * @return
     */
    @Override
    public void error(ModifyOrderRequest modifyOrderRequest) {
        String orderId = modifyOrderRequest.getOrderId();
        OrderResponse orderResponse =queryOrder(orderId);
        if(null == orderResponse){
            return;
        }
        /**
         * 删除该订单发配送的调度缓存
         */
        redisService.lrem(OrderStatus.FLOW + OrderStatus.ACCOUNT_PAID,orderId);
        /**
         * 将要修改的的值赋值给orderResponse
         */
        orderResponse.setExceptionStatus(/*modifyOrderRequest.getExceptionStatus()*/OrderStatus.DISTRIBUTION);
        orderResponse.setExceptionRemark(modifyOrderRequest.getExceptionRemark());
        orderResponse.setSendExceptionTime(new Date());
        /**
         * 将变化后的订单状态存入redis sorted set(允许查询一次)
         */
        notifyService.sendNotify(orderResponse);
    }

    /**
     * 该异常是由于配送不通过，或者第三方公司取消订单等原因,急需处理的订单
     * 修改属性：异常编码,异常信息,以及时间
     * @param modifyOrderRequest
     * @return
     */
    @Override
    public void anomaly(ModifyOrderRequest modifyOrderRequest) {
        String orderId = modifyOrderRequest.getOrderId();
        OrderResponse orderResponse =queryOrder(orderId);
        if(null == orderResponse){
            return;
        }
        Integer times = orderResponse.getTimes()+1;
        orderResponse.setTimes(times);
        orderResponse.setExceptionStatus(/*modifyOrderRequest.getExceptionStatus()*/OrderStatus.DELIVERY_ERROR);
        /**
         * 如果连续配送五次失败,请厨房端处理
         */
        if(times > 5){
            /**
             * 将该订单从发起配送状态中删除
             */
            redisService.lrem(OrderStatus.FLOW+OrderStatus.ACCOUNT_PAID,orderId);
            /**
             * 同步数据库
             */
            executorService.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
        }else{
            /**
             * 重新发起配送
             */
            redisService.lrem(OrderStatus.FLOW+OrderStatus.ACCOUNT_PAID,orderId);
            redisService.lpush(OrderStatus.FLOW+OrderStatus.ACCOUNT_PAID,orderId,7200);
            orderResponse.setTotalStatus(OrderStatus.INDEVELOPMENT);
        }
        orderResponse.setExceptionRemark(modifyOrderRequest.getExceptionRemark());
        /**
         * 将新的订单状态存入缓存,并发给厨房
         */
        notifyService.sendNotify(orderResponse);
    }

    /**
     * 订单已完成的接口,当骑手完成订单,可以使用改接口回调
     * 修改属性：送达时间,送餐员姓名,骑手电话,订单状态,配送状态
     * @param modifyOrderRequest
     * @return
     */
    @Override
    public void complete(ModifyOrderRequest modifyOrderRequest) throws JsonProcessingException {
        OrderResponse orderResponse =queryOrder(modifyOrderRequest.getOrderId());
        if(null == orderResponse){
            return;
        }
        orderResponse.setTotalStatus(OrderStatus.SEND_COMPLETE);
        orderResponse.setOrderStatus(OrderStatus.DIS_COMPLETE);
        orderResponse.setSendStatus(OrderStatus.SEND_COMPLETE);
        orderResponse.setTaskTime(modifyOrderRequest.getTaskTime());
        orderResponse.setTaskUserName(modifyOrderRequest.getTaskUserName());
        orderResponse.setTaskUserPhone(modifyOrderRequest.getTaskUserPhone());
        redisService.lpush(OrderStatus.COMPLETE+orderResponse.getStoreId(), MapperUtils.obj2jsonIgnoreNull(orderResponse),90000);
        notifyService.sendNotify(orderResponse);
        executorService.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
    }

    /**
     * 订单正在配送中,该回调是骑手已经接单,同时可能有些配送公司的回调原因,pickup接口不回调
     * @param modifyOrderRequest
     * @return
     */
    @Override
    public void delivery(ModifyOrderRequest modifyOrderRequest) throws Exception {
        OrderResponse orderResponse =queryOrder(modifyOrderRequest.getOrderId());
        if(null == orderResponse){
            return;
        }
        /**
         * 可能不会调用pickup接口,所以本接口也得修改订单状态以及totalStatus以及取货时间(打包时间)
         */
        orderResponse.setTotalStatus(OrderStatus.REFUND_ORDERS);
        orderResponse.setOrderStatus(OrderStatus.DISTRIBUTION);
        orderResponse.setSendStatus(OrderStatus.REFUND_ORDERS);
        orderResponse.setTaskUserName(modifyOrderRequest.getTaskUserName());
        orderResponse.setTaskUserPhone(modifyOrderRequest.getTaskUserPhone());
        orderResponse.setPackUserTime(new Date());
        String key = OrderStatus.CANCEL+modifyOrderRequest.getOrderId();
        if(redisService.exists(key)){
            tryCancanOrder(key,orderResponse);
        }
        notifyService.sendNotify(orderResponse);
        executorService.submit(new OrderModifyTask(modifyOrderRequest,orderClient));

    }

    /**
     * 骑手到店或者取货接口,该回调是骑手已经到店，已经接受订单
     * @param modifyOrderRequest
     * @return
     */
    @Override
    public void pickup(ModifyOrderRequest modifyOrderRequest) throws Exception {
        OrderResponse orderResponse =queryOrder(modifyOrderRequest.getOrderId());
        if(null == orderResponse){
            return;
        }
        orderResponse.setTotalStatus(OrderStatus.KITCHEN_ERROR);
        orderResponse.setOrderStatus(OrderStatus.DISTRIBUTION);
        orderResponse.setSendStatus(OrderStatus.KITCHEN_ERROR);
        orderResponse.setTaskUserName(modifyOrderRequest.getTaskUserName());
        orderResponse.setTaskUserPhone(modifyOrderRequest.getTaskUserPhone());
        orderResponse.setPackUserTime(new Date());
        String key = OrderStatus.CANCEL+modifyOrderRequest.getOrderId();
        if(redisService.exists(key)){
            tryCancanOrder(key,orderResponse);
        }
        notifyService.sendNotify(orderResponse);
        executorService.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
    }

    /**
     * 骑手已接单,该回调是第三方公司发完配送，骑手已经接单的回调
     * @param modifyOrderRequest
     * @return
     */
    @Override
    public void taskOrder(ModifyOrderRequest modifyOrderRequest) throws Exception {
        OrderResponse orderResponse = queryOrder(modifyOrderRequest.getOrderId());
        if(null == orderResponse){
            return;
        }
        orderResponse.setSendStatus(OrderStatus.DELIVERY_ERROR);
        orderResponse.setTotalStatus(OrderStatus.DELIVERY_ERROR);
        orderResponse.setTaskUserName(modifyOrderRequest.getTaskUserName());
        orderResponse.setTaskUserPhone(modifyOrderRequest.getTaskUserPhone());
        orderResponse.setTaskUserTime(new Date());
        String key = OrderStatus.CANCEL+modifyOrderRequest.getOrderId();
        if(redisService.exists(key)){
            tryCancanOrder(key,orderResponse);
        }
        notifyService.sendNotify(orderResponse);
        executorService.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
    }

    /**
     * 待调度，待分配骑手
     * @param modifyOrderRequest
     */
    @Override
    public void dispatcherRider(ModifyOrderRequest modifyOrderRequest) {
        OrderResponse orderResponse = queryOrder(modifyOrderRequest.getOrderId());
        if(null == orderResponse){
            return;
        }
        orderResponse.setSendName(modifyOrderRequest.getSendName());
        orderResponse.setSendStatus(OrderStatus.DIS_COMPLETE);
        orderResponse.setTotalStatus(OrderStatus.DIS_COMPLETE);
        orderResponse.setDeliveryId(null != modifyOrderRequest.getDeliveryId()?modifyOrderRequest.getDeliveryId():null);
        orderResponse.setMtPeisongId(null != modifyOrderRequest.getMtPeisongId()?modifyOrderRequest.getMtPeisongId():null);
        redisService.lrem(OrderStatus.FLOW+OrderStatus.ACCOUNT_PAID,modifyOrderRequest.getOrderId());
        notifyService.sendNotify(orderResponse);
        executorService.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
    }


    /**
     * 订单发起配送成功,该回调由第三方公司接受订单后发起的回调
     * @param modifyOrderRequest
     * @return
     */
    @Override
    public void haveOrder(ModifyOrderRequest modifyOrderRequest) {
        OrderResponse orderResponse = queryOrder(modifyOrderRequest.getOrderId());
        if(null == orderResponse){
            return;
        }
        orderResponse.setSendName(modifyOrderRequest.getSendName());
        orderResponse.setSendStatus(OrderStatus.DISTRIBUTION);
        orderResponse.setTotalStatus(OrderStatus.DISTRIBUTION);
        redisService.lrem(OrderStatus.FLOW+OrderStatus.ACCOUNT_PAID,modifyOrderRequest.getOrderId());
        notifyService.sendNotify(orderResponse);
        executorService.submit(new OrderModifyTask(modifyOrderRequest,orderClient));
    }

    /**
     * 提取公共取值方法
     * @param orderId
     * @return
     * @throws IOException
     */
    private OrderResponse queryOrder(String orderId){
        /**
         * 从ehcache中取值,由于存值的时候是直接存orderResponse对象,取出时可直接强转
         */
        OrderResponse orderResponse = (OrderResponse) ehCacheService.getObj(orderId);
        if (null == orderResponse) {
            /**
             * 从order模块取值
             */
            ReturnModel returnModel = orderClient.detail(orderId);
            orderResponse = null != returnModel.getData()? MapperUtils.obj2pojo(returnModel.getData(),OrderResponse.class):null;
        }
        return orderResponse;
    }

    /**
     * 尝试取消挂起的订单
     * @param orderResponse
     * @throws Exception
     */
    private void tryCancanOrder(String key,OrderResponse orderResponse) throws Exception {
        String value = redisService.watch(key);
        if("OK".equalsIgnoreCase(value)){
            /**
             * redis 事物
             */
            Transaction t = redisService.multi();
            redisService.setString(key,"2");
            /**
             * redis 事物是否执行成功
             */
            if(null != t.exec()){
                /**
                 * 取消订单
                 */
                ReturnModel returnModel = cancelOrder(orderResponse);
                /**
                 * 备注：取消订单成功时，系统内部将订单置为6,但第三方回调将订单置为5,注意订单处理;
                 */
                if(returnModel.getCode() == 200){
                    redisService.setString(key,"3");
                    orderResponse.setCancelCode(3);
                    orderResponse.setTotalStatus(6);
                }else{
                    redisService.setString(key,"4");
                    orderResponse.setCancelCode(4);
                }
            }
        }
    }
}
