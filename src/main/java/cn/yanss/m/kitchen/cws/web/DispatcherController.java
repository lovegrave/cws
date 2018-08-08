package cn.yanss.m.kitchen.cws.web;

import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.service.DispatcherService;
import common.returnModel.ReturnModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kitchen/api/dispatcher")
public class DispatcherController {

    @Autowired
    private DispatcherService dispatcherService;

    /**
     * 配送调度获取订单接口
     *
     * @return
     */
    @PostMapping("/gainOrder")
    public ReturnModel gainOrder() {
        return dispatcherService.gainOrder();
    }

    @PostMapping("/callback")
    public ReturnModel callback(@RequestBody ModifyOrderRequest modifyOrderRequest) {
        return dispatcherService.callback(modifyOrderRequest);
    }



}

