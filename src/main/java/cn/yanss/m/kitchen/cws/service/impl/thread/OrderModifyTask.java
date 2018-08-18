package cn.yanss.m.kitchen.cws.service.impl.thread;


import cn.yanss.m.kitchen.cws.api.OrderClient;
import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Callable;


/**
 * @author
 */
@Log4j2
public class OrderModifyTask implements Callable<ReturnModel> {
    private OrderClient orderClient;
    private ModifyOrderRequest modifyOrderRequest;

    public OrderModifyTask(ModifyOrderRequest modifyOrderRequest, OrderClient orderClient) {
        this.modifyOrderRequest = modifyOrderRequest;
        this.orderClient = orderClient;
    }

    @Override
    public ReturnModel call(){
        try{
            return orderClient.modifyOrderStatus(modifyOrderRequest);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ReturnModel();
        }
    }
}
