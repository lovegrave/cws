package cn.yanss.m.kitchen.cws.utils;


import cn.yanss.m.kitchen.cws.entity.request.ModifyOrderRequest;
import cn.yanss.m.kitchen.cws.entity.response.OrderResponse;

/**
 * @author HL
 */
public class ObjectSwitch {

    public static void switchCopy(OrderResponse order, ModifyOrderRequest m){
        Integer exceptionStatus = m.getExceptionStatus();
        if(null != exceptionStatus){
            order.setExceptionStatus(exceptionStatus);
        }
        if(null != m.getDeliveryId()){
            order.setDeliveryId(m.getDeliveryId());
        }
        if(null!= m.getExceptionRemark()){
            order.setExceptionRemark(m.getExceptionRemark());
        }
        if(null != m.getMtPeisongId()){
            order.setMtPeisongId(m.getMtPeisongId());
        }
        if(null != m.getOrderStatus()){
            order.setOrderStatus(m.getOrderStatus());
        }
        if(null != m.getPackUserTime()){
            order.setPackUserTime(m.getPackUserTime());
        }
        if(null != m.getSendName()){
            order.setSendName(m.getSendName());
        }
        if(null != m.getSendStatus()){
            order.setSendStatus(m.getSendStatus());
        }
        if(null != m.getTaskTime()){
            order.setTaskTime(m.getTaskTime());
        }
        if(null != m.getTaskUserId()){
            order.setTaskUserId(m.getTaskUserId());
        }
        if(null != m.getTaskTime()){
            order.setTaskTime(m.getTaskTime());
        }
        if(null != m.getTaskUserName()){
            order.setTaskUserName(m.getTaskUserName());
        }
        if(null != m.getTaskUserPhone()){
            order.setTaskUserPhone(m.getTaskUserPhone());
        }
        if(null != m.getTaskUserTime()){
            order.setTaskUserTime(m.getTaskUserTime());
        }
    }

}
