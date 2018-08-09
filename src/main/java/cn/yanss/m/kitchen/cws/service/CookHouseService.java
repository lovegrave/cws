package cn.yanss.m.kitchen.cws.service;

import cn.yanss.m.kitchen.cws.entity.request.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.returnModel.ReturnModel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface CookHouseService {

    ReturnModel findOrderList(OrderRequest orderRequest);

    ReturnModel findOrderDetail(String orderId, Integer storeId);

    ReturnModel opt(String orderId);

    ReturnModel orderException(String orderId) throws Exception;

    ReturnModel abandonCancel(String orderId);

    ReturnModel orderFinish(String orderId) throws JsonProcessingException, ExecutionException, InterruptedException;

    ReturnModel applicationForRefund(String orderId) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException;
}