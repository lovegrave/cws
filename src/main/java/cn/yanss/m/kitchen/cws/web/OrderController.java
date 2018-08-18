package cn.yanss.m.kitchen.cws.web;

import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.service.OrderService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
}
