package cn.yanss.m.kitchen.cws.web;

import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.service.OrderService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/kitchen/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 补单接口
     *
     * @param orderResponseList
     * @return
     */
    @PostMapping("/addOrder")
    public ReturnModel addOrder(@RequestBody JSONArray orderResponseList) throws Exception {
        return orderService.addOrder(orderResponseList);
    }

    /**
     * 催单
     * @param orderId
     * @return
     */
    @GetMapping("/reminder")
    public ReturnModel reminder(String orderId){
        return orderService.reminder(orderId);
    }

    /**
     * 申请退款
     * @param orderId
     * @return
     */
    @GetMapping("/refund")
    public ReturnModel refund(String orderId){
        return orderService.refund(orderId);
    }

    @GetMapping("/cancelRefund")
    public ReturnModel cancelRefund(String orderId){
        return orderService.cancelRefund(orderId);
    }
}
