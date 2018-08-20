package cn.yanss.m.kitchen.cws.service;

import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.entity.request.OrderRequest;
import cn.yanss.m.kitchen.cws.entity.request.StoreRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface CookHouseService {

    ReturnModel findOrderList(OrderRequest orderRequest);

    ReturnModel findOrderDetail(String orderId, Integer storeId);

    ReturnModel opt(String orderId);

    ReturnModel orderException(String orderId) throws Exception;

    ReturnModel abandonCancel(String orderId);

    ReturnModel orderFinish(String orderId) throws JsonProcessingException, ExecutionException, InterruptedException;

    ReturnModel cancelOrderAndRefund(String orderId);

    ReturnModel applicationForRefund(String orderId) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException;

    ReturnModel findStoreId(StoreRequest storeRequest);
}
