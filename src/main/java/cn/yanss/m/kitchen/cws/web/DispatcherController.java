package cn.yanss.m.kitchen.cws.web;

import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.service.DispatcherService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public ReturnModel callback(@RequestBody ModifyOrderRequest modifyOrderRequest) throws Exception {
        return dispatcherService.callback(modifyOrderRequest);
    }
    /**
     * 已接单
     * @return
     */
    @PostMapping("/haveOrder")
    public void haveOrder(@RequestBody ModifyOrderRequest modifyOrderRequest) {
        dispatcherService.haveOrder(modifyOrderRequest);
    }

    /**
     * 骑手已接单
     * @return
     */
    @PostMapping("/taskOrder")
    public void taskOrder(@RequestBody ModifyOrderRequest modifyOrderRequest) throws Exception {
        dispatcherService.taskOrder(modifyOrderRequest);
    }

    /**
     * 骑手已到店,已取货
     * @return
     */
    @PostMapping("/pickup")
    public void pickup(@RequestBody ModifyOrderRequest modifyOrderRequest) throws Exception {
        dispatcherService.pickup(modifyOrderRequest);
    }

    /**
     * 已配送
     * @return
     */
    @PostMapping("/delivery")
    public void delivery(@RequestBody ModifyOrderRequest modifyOrderRequest) throws Exception {
      dispatcherService.delivery(modifyOrderRequest);
    }

    /**
     * 配送完成
     * @return
     */
    @PostMapping("/complete")
    public void complete(@RequestBody ModifyOrderRequest modifyOrderRequest) throws JsonProcessingException {
        dispatcherService.complete(modifyOrderRequest);
    }

    /**
     * 可再次配发的异常，订单取消
     * @return
     */
    @PostMapping("/anomaly")
    public void anomaly(@RequestBody ModifyOrderRequest modifyOrderRequest) {
        dispatcherService.anomaly(modifyOrderRequest);
    }

    /**
     * 订单运行时异常
     * @return
     */
    @PostMapping("/error")
    public void error(@RequestBody ModifyOrderRequest modifyOrderRequest) {
       dispatcherService.error(modifyOrderRequest);
    }



}

