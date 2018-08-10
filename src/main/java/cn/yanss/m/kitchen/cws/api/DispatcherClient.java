package cn.yanss.m.kitchen.cws.api;

import cn.yanss.m.kitchen.cws.api.config.FeignConfig;
import cn.yanss.m.kitchen.cws.api.hystrix.DispatcherHystrix;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import common.returnModel.ReturnModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "yanss-dispatcher", url = "http://kitchen-express-scheduler:8081",configuration = FeignConfig.class,fallback = DispatcherHystrix.class)
public interface DispatcherClient {

    @PostMapping("/api/v1/kitchen/express/scheduler/cancelOrder")
    ReturnModel cancelCode(@RequestBody OrderResponse orderResponse);

    @PostMapping("/api/v1/kitchen/express/scheduler/createOrder")
    ReturnModel createOrder(@RequestBody OrderResponse jsonObject);

}
