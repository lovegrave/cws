package cn.yanss.m.kitchen.cws.disruptor;

import cn.yanss.m.kitchen.cws.cache.EhCacheServiceImpl;
import cn.yanss.m.kitchen.cws.cache.RedisService;
import cn.yanss.m.kitchen.cws.disruptor.exception.NotifyEventHandlerException;
import cn.yanss.m.kitchen.cws.disruptor.factory.NotifyEventFactory;
import cn.yanss.m.kitchen.cws.disruptor.handler.NotifyKitchenEventHandler;
import cn.yanss.m.kitchen.cws.disruptor.handler.NotifyCacheEventHandler;
import cn.yanss.m.kitchen.cws.disruptor.pojo.NotifyMessage;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import cn.yanss.m.kitchen.cws.websocket.message.PushMessage;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

@Service
public class NotifyServiceImpl implements DisposableBean,InitializingBean {
    private Disruptor<NotifyMessage> disruptor;
    private static final int RING_BUFFER_SIZE = 1024 * 1024;
    @Autowired
    private EhCacheServiceImpl ehCacheService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private PushMessage pushMessage;

    @Override
    public void destroy() throws Exception {
        disruptor.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        disruptor = new Disruptor<>(new NotifyEventFactory(),RING_BUFFER_SIZE, Executors.defaultThreadFactory(), ProducerType.SINGLE,new BlockingWaitStrategy());
        disruptor.setDefaultExceptionHandler(new NotifyEventHandlerException());
        EventHandlerGroup<NotifyMessage> handlerGroup = disruptor.handleEventsWith(new NotifyCacheEventHandler(ehCacheService,redisService));
        handlerGroup.then(new NotifyKitchenEventHandler(pushMessage,redisService));
        disruptor.start();
    }

    public void sendNotify(OrderResponse orderResponse) {
        RingBuffer<NotifyMessage> ringBuffer = disruptor.getRingBuffer();
        /**
         * lambda式写法
         */
        ringBuffer.publishEvent((event, sequence, data) -> event.setOrderResponse(data), orderResponse);

    }
}
