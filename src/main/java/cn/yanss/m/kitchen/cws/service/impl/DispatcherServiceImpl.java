package cn.yanss.m.kitchen.cws.service.impl;

import cn.yanss.m.kitchen.cws.api.DispatcherClient;
import cn.yanss.m.kitchen.cws.api.OrderClient;
import cn.yanss.m.kitchen.cws.cache.EhCacheServiceImpl;
import cn.yanss.m.kitchen.cws.cache.RedisService;
import cn.yanss.m.kitchen.cws.constant.OrderStatus;
import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import cn.yanss.m.kitchen.cws.service.CollectionService;
import cn.yanss.m.kitchen.cws.service.DispatcherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.returnModel.ReturnModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DispatcherServiceImpl implements DispatcherService {

    @Autowired
    private CollectionService collectionService;

    private final OrderClient orderClient;
    private final DispatcherClient dispatcherClient;
    @Autowired
    private RedisService redisService;
    @Autowired
    private EhCacheServiceImpl ehCacheService;

    @Autowired
    public DispatcherServiceImpl(OrderClient orderClient,DispatcherClient dispatcherClient) {
        this.orderClient = orderClient;
        this.dispatcherClient = dispatcherClient;
    }
    @Override
    public ReturnModel gainOrder() {
        return null;
    }

    @Override
    public ReturnModel callback(ModifyOrderRequest modifyOrderRequest) {
        return null;
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
}
