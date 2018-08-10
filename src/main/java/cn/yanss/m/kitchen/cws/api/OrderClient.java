package cn.yanss.m.kitchen.cws.api;


import cn.yanss.m.kitchen.cws.api.config.FeignConfig;
import cn.yanss.m.kitchen.cws.api.hystrix.OrderHystrix;
import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.entity.request.OrderRequest;
import common.returnModel.ReturnModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "yanss-order", url = "192.168.11.214:8086",configuration = FeignConfig.class,fallback = OrderHystrix.class)
public interface OrderClient {

    @GetMapping("/api/v1/order/detail")
    ReturnModel detail(@RequestParam("orderId") String orderId);

    @PostMapping("/api/v1/order/modify")
    ReturnModel modifyOrderStatus(@RequestBody ModifyOrderRequest modifyOrderRequest);

    @PostMapping("/api/v1/order/list")
    ReturnModel findHistoryOrderList(OrderRequest orderRequest);

}
