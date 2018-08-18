package cn.yanss.m.kitchen.cws.service.impl;

import cn.yanss.m.kitchen.cws.api.OrderClient;
import cn.yanss.m.kitchen.cws.cache.EhCacheServiceImpl;
import cn.yanss.m.kitchen.cws.cache.RedisService;
import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.constant.OrderStatus;
import cn.yanss.m.kitchen.cws.disruptor.NotifyServiceImpl;
import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import cn.yanss.m.kitchen.cws.service.OrderService;
import cn.yanss.m.kitchen.cws.utils.DateUtil;
import cn.yanss.m.kitchen.cws.utils.MapperUtils;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Service
public class OrderServiceImpl implements OrderService {

    private final OrderClient orderClient;
    private final RedisService redisService;
    private final EhCacheServiceImpl ehCacheService;
    private final NotifyServiceImpl notifyService;

    @Autowired
    public OrderServiceImpl(OrderClient orderClient, RedisService redisService, EhCacheServiceImpl ehCacheService, NotifyServiceImpl notifyService) {
        this.orderClient = orderClient;
        this.redisService = redisService;
        this.ehCacheService = ehCacheService;
        this.notifyService = notifyService;
    }

    /**
     * 补单接口
     *
     * @param
     */
    @Override
    public ReturnModel addOrder(JSONArray str) throws IOException {
        List<OrderResponse> orderResponseList = MapperUtils.json2list(MapperUtils.obj2json(str),OrderResponse.class);
        try {
            for (OrderResponse orderResponse : orderResponseList) {
                Long serviceTime = orderResponse.getServiceTime().getTime();
                if( orderResponse.getBookingType() != 0 && serviceTime + 10800000 < System.currentTimeMillis() ){
                    ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest();
                    modifyOrderRequest.setOrderId(orderResponse.getOrderId());
                    modifyOrderRequest.setExceptionStatus(OrderStatus.ERROR_STATUS);
                    modifyOrderRequest.setExceptionRemark("订单超时");
                    modifyOrderRequest.setSendName("");
                    orderClient.modifyOrderStatus(modifyOrderRequest);
                }else{
                    if (orderResponse.getBookingType() == 0 && serviceTime > System.currentTimeMillis()+ 7200000) {
                        /**
                         * 预定单处理,待编写
                         */
                        String key = DateUtil.DateToString(orderResponse.getServiceTime());
                        Long time = System.currentTimeMillis() - orderResponse.getServiceTime().getTime();
                        int seconds = (int) (time / 1000 + 72000);
                        redisService.lpush(key,orderResponse.getOrderId(), seconds);
                    }else{
                        if(orderResponse.getOrderStatus() > 1 || ehCacheService.exists(orderResponse.getOrderId())){
                            return new ReturnModel();
                        }
                        /**
                         * 模拟订单号
                         */
                        orderResponse.setOrderId("PT"+UUID.randomUUID().toString().replace("-",""));

                        Integer totalStatus = orderResponse.getTotalStatus();
                        if(null != totalStatus && totalStatus > 1){
                            orderResponse.setBeforeStatus(totalStatus);
                        }else{
                            orderResponse.setBeforeStatus(OrderStatus.ACCOUNT_PAID);
                        }
                        orderResponse.setTotalStatus(OrderStatus.ACCOUNT_PAID);
                        /**
                         * 设置取货号
                         */
                        String orderNo = orderResponse.getOrderNo();
                        if(StringUtils.isEmpty(orderNo)){
                            orderNo = String.valueOf(1000+redisService.incr(DateUtil.getStartTime()+orderResponse.getStoreId(),90000));
                            orderResponse.setOrderNo(orderNo);
                        }
                        orderResponse.setOrderPick(orderNo);
                        /**
                         * 将取货号与订单号关联,便于订单的查询
                         */
                        redisService.setMapString(OrderStatus.ORDER_PICK+orderResponse.getStoreId(),orderResponse.getOrderPick(),orderResponse.getOrderId(),10800);
                        /**
                         * 设置订单配送状态
                         */
                        orderResponse.setStatus(orderResponse.getTotalStatus());
                        orderResponse.setSendStatus(1);
                        notifyService.sendNotify(orderResponse);

                    }
                }
            }
            return new ReturnModel();
        } catch (Exception e) {
            return new ReturnModel(500, "数据存储错误");
        }
    }
}
