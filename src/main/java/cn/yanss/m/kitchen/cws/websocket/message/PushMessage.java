package cn.yanss.m.kitchen.cws.websocket.message;

import cn.yanss.m.kitchen.cws.utils.MapperUtils;
import cn.yanss.m.kitchen.cws.websocket.link.Global;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;


@Component
@Log4j2
public class PushMessage {

    /**
     * netty 推送
     * @param channelId
     * @param obj
     * @return
     */
    public boolean sendMessage(String channelId,Object obj) {
        Channel channel = Global.get(channelId);
        if(null == channel){
            return false;
        }
        try {
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(MapperUtils.obj2json(obj));
            ChannelFuture channelFuture = channel.writeAndFlush(textWebSocketFrame);
            return channelFuture.isDone();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
