package cn.yanss.m.kitchen.cws.service;

import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.returnModel.ReturnModel;

import java.io.IOException;

public interface DispatcherService {
    ReturnModel gainOrder();

    ReturnModel callback(ModifyOrderRequest modifyOrderRequest) throws Exception;

    ReturnModel cancelOrder(String orderId, Integer cancelCode) throws IOException;

    ReturnModel cancelOrder(OrderResponse orderResponse);

    ReturnModel error(ModifyOrderRequest modifyOrderRequest);

    ReturnModel anomaly(ModifyOrderRequest modifyOrderRequest);

    ReturnModel complete(ModifyOrderRequest modifyOrderRequest) throws JsonProcessingException;

    ReturnModel delivery(ModifyOrderRequest modifyOrderRequest);

    ReturnModel pickup(ModifyOrderRequest modifyOrderRequest) throws Exception;

    ReturnModel taskOrder(ModifyOrderRequest modifyOrderRequest) throws Exception;

    ReturnModel haveOrder(ModifyOrderRequest modifyOrderRequest);
}
