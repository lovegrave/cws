package cn.yanss.m.kitchen.cws.disruptor.handler;

import cn.yanss.m.kitchen.cws.cache.RedisService;
import cn.yanss.m.kitchen.cws.constant.OrderStatus;
import cn.yanss.m.kitchen.cws.disruptor.pojo.NotifyMessage;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import cn.yanss.m.kitchen.cws.websocket.message.PushMessage;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;

@Log4j2
public class NotifyKitchenEventHandler implements EventHandler<NotifyMessage>,WorkHandler<NotifyMessage> {

    private PushMessage pushMessage;
    private RedisService redisService;


    public NotifyKitchenEventHandler(PushMessage pushMessage,RedisService redisService) {
        this.pushMessage = pushMessage;
        this.redisService = redisService;
    }

    @Override
    public void onEvent(NotifyMessage notifyMessage, long l, boolean b) throws Exception {
        this.onEvent(notifyMessage);
    }

    @Override
    public void onEvent(NotifyMessage notifyMessage) throws Exception {
        /**
         * 厨房端逻辑处理
         */
        OrderResponse orderResponse = notifyMessage.getOrderResponse();
        log.info(Thread.currentThread());
        boolean success = pushMessage.sendMessage(String.valueOf(orderResponse.getStoreId()), Collections.singletonList(orderResponse));
        if(!success){
            redisService.lpush(OrderStatus.STOREKEY+orderResponse.getStoreId(),orderResponse.getOrderId(),3600);
        }
    }
}
