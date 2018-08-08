package cn.yanss.m.kitchen.cws.service;

import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.returnModel.ReturnModel;

import java.io.IOException;

public interface DispatcherService {
    ReturnModel gainOrder();

    ReturnModel callback(ModifyOrderRequest modifyOrderRequest);

    ReturnModel cancelOrder(String orderId, Integer cancelCode) throws IOException;
}
