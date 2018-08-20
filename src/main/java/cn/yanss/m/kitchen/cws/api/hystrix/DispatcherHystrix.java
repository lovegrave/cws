package cn.yanss.m.kitchen.cws.api.hystrix;

import cn.yanss.m.kitchen.cws.api.DispatcherClient;
import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class DispatcherHystrix implements DispatcherClient {
    @Override
    public ReturnModel cancelCode(OrderResponse orderResponse) {
        log.error("调度模块--->cancelCode接口调用错误");
        return new ReturnModel(500,"接口错误");
    }

    @Override
    public ReturnModel createOrder(OrderResponse jsonObject) {
        log.error("调度模块--->createOrder接口调用错误");
        return new ReturnModel(500,"调用接口失败");
    }
}
