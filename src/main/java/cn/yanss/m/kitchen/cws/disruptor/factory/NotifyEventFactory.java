package cn.yanss.m.kitchen.cws.disruptor.factory;


import cn.yanss.m.kitchen.cws.disruptor.pojo.NotifyMessage;
import com.lmax.disruptor.EventFactory;

public class NotifyEventFactory implements EventFactory<NotifyMessage> {
    @Override
    public NotifyMessage newInstance() {
        return new NotifyMessage();
    }
}
