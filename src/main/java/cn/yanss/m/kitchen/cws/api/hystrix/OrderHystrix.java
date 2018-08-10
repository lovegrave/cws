package cn.yanss.m.kitchen.cws.api.hystrix;

import cn.yanss.m.kitchen.cws.api.OrderClient;
import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.entity.request.OrderRequest;
import common.returnModel.ReturnModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Component
@Log4j2
public class OrderHystrix implements OrderClient {
    @Override
    public ReturnModel detail(String orderId) {
        log.error("订单模块--->detail接口调用失败");
        return new ReturnModel(500,"接口调用失败");
    }

    @Override
    public ReturnModel modifyOrderStatus(ModifyOrderRequest modifyOrderRequest) {
        log.error("订单模块--->修改订单模块调用失败");
        return new ReturnModel();
    }

    @Override
    public ReturnModel findHistoryOrderList(OrderRequest orderRequest) {
        log.error("订单模块--->查询订单接口调用失败");
        return new ReturnModel();
    }
}
