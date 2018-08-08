package cn.yanss.m.kitchen.cws.disruptor.pojo;

import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;
import lombok.Data;

@Data
public class NotifyMessage {

    private OrderResponse orderResponse;
}
