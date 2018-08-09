package cn.yanss.m.kitchen.cws.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单修改数据封装类
 */
@Data
public class ModifyOrderRequest implements Serializable{

    private String orderId;
    private Integer orderStatus;
    private String orderNo;
    private Integer sendStatus;
    private Integer exceptionStatus;
    private String exceptionRemark;
    private String mtPeisongId;
    private Long deliveryId;
    private Integer taskUserId;
    private String taskUserName;
    private String taskUserPhone;
    private String sendName;
    private Date packUserTime;
    private Date taskTime;
    private Date taskUserTime;
}
