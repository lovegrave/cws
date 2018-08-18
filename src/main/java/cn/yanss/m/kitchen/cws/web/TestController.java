package cn.yanss.m.kitchen.cws.web;

import cn.yanss.m.kitchen.cws.common.ReturnModel;
import cn.yanss.m.kitchen.cws.disruptor.NotifyServiceImpl;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TestController {
    @Autowired
    private NotifyServiceImpl notifyService;
    @RequestMapping("/pull")
    public void push() throws InterruptedException {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(UUID.randomUUID().toString().replace("-",""));
        orderResponse.setStoreId(1);
        orderResponse.setOrderPick("1001");
        orderResponse.setOrderStatus(2);
        orderResponse.setExceptionRemark("别忘记分别为方便就被文件发布发布往期副本我去帮我就方便我反驳我我就南非警方帮我发吧我能成为非我非我成为继防备发给我那我服务服务i本金翻倍翻倍服务");
        orderResponse.setSendName("是你肯定不服无法把握农家妇女被关闭北京v欸个贝瑞根本v金贝贝而本届u个如果被人给");
        Long time = System.currentTimeMillis();
//        for (int i = 0; i < 1000; i++) {
            notifyService.sendNotify(orderResponse);
//        }
        System.err.println(System.currentTimeMillis()-time);
    }
    @RequestMapping("/pull2")
    public ReturnModel push2() throws InterruptedException {
        Thread.sleep(50000);
        return new ReturnModel("push2");
    }

}
