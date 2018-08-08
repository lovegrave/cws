package cn.yanss.m.kitchen.cws.disruptor.handler;

import cn.yanss.m.kitchen.cws.cache.EhCacheServiceImpl;
import cn.yanss.m.kitchen.cws.cache.RedisService;
import cn.yanss.m.kitchen.cws.disruptor.pojo.NotifyMessage;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NotifyCacheEventHandler implements EventHandler<NotifyMessage>,WorkHandler<NotifyMessage> {

    private EhCacheServiceImpl ehCacheService;
    private RedisService redisService;

    public NotifyCacheEventHandler(EhCacheServiceImpl ehCacheService, RedisService redisService) {
        this.ehCacheService = ehCacheService;
        this.redisService = redisService;
    }

    @Override
    public void onEvent(NotifyMessage notifyMessage, long l, boolean b) throws Exception {
        this.onEvent(notifyMessage);
    }

    @Override
    public void onEvent(NotifyMessage notifyMessage) throws Exception {
        /**
         * 订单模块逻辑
         */
        OrderResponse orderResponse = notifyMessage.getOrderResponse();
        /**
         * 将订单存储在ehcache缓存中
         */

        redisService.zrem(orderResponse.getStoreId()+""+orderResponse.getBeforeStatus(),orderResponse.getOrderId());
        /**
         * 将订单状态关联订单号放入redis sorted set 集合中,厨房通过该集合查询该状态的实时订单
         */
        orderResponse.setBeforeStatus(orderResponse.getTotalStatus());
        ehCacheService.save(orderResponse.getOrderId(),orderResponse);
        redisService.zadd(orderResponse.getStoreId()+""+orderResponse.getTotalStatus(),Double.valueOf(orderResponse.getOrderPick()),orderResponse.getOrderId(),7200);
    }
}
