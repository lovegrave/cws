package cn.yanss.m.kitchen.cws.websocket.link;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class Global {
    private Global() {
    }

    public final static ConcurrentHashMap<String,Channel> channel = new ConcurrentHashMap<>(32);

    public static void put(String id, Channel socketChannel) {
        channel.put(id, socketChannel);
    }

    public static Channel get(String id) {
        return channel.get(id);
    }

    public static void remove(Channel nioSocketChannel) {
        channel.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> channel.remove(entry.getKey()));
    }
}
