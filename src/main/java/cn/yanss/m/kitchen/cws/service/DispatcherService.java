package cn.yanss.m.kitchen.cws.service;

import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import com.fasterxml.jackson.core.JsonProcessingException;


import java.io.IOException;

public interface DispatcherService {
    ReturnModel gainOrder();

    ReturnModel callback(ModifyOrderRequest modifyOrderRequest) throws Exception;

    ReturnModel cancelOrder(String orderId, Integer cancelCode) throws IOException;

    ReturnModel cancelOrder(OrderResponse orderResponse);

    void error(ModifyOrderRequest modifyOrderRequest);

    void anomaly(ModifyOrderRequest modifyOrderRequest);

    void complete(ModifyOrderRequest modifyOrderRequest) throws JsonProcessingException;

    void delivery(ModifyOrderRequest modifyOrderRequest) throws Exception;

    void pickup(ModifyOrderRequest modifyOrderRequest) throws Exception;

    void taskOrder(ModifyOrderRequest modifyOrderRequest) throws Exception;

    void haveOrder(ModifyOrderRequest modifyOrderRequest);
}
