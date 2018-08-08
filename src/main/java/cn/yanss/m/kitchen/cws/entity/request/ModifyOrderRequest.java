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


    public Date getPackUserTime() {
        return packUserTime;
    }

    public void setPackUserTime(Date packUserTime) {
        this.packUserTime = packUserTime;
    }

    public Date getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(Date taskTime) {
        this.taskTime = taskTime;
    }

    public Date getTaskUserTime() {
        return taskUserTime;
    }

    public void setTaskUserTime(Date taskUserTime) {
        this.taskUserTime = taskUserTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Integer getExceptionStatus() {
        return exceptionStatus;
    }

    public void setExceptionStatus(Integer exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }

    public String getExceptionRemark() {
        return exceptionRemark;
    }

    public void setExceptionRemark(String exceptionRemark) {
        this.exceptionRemark = exceptionRemark;
    }

    public String getMtPeisongId() {
        return mtPeisongId;
    }

    public void setMtPeisongId(String mtPeisongId) {
        this.mtPeisongId = mtPeisongId;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Integer getTaskUserId() {
        return taskUserId;
    }

    public void setTaskUserId(Integer taskUserId) {
        this.taskUserId = taskUserId;
    }

    public String getTaskUserName() {
        return taskUserName;
    }

    public void setTaskUserName(String taskUserName) {
        this.taskUserName = taskUserName;
    }

    public String getTaskUserPhone() {
        return taskUserPhone;
    }

    public void setTaskUserPhone(String taskUserPhone) {
        this.taskUserPhone = taskUserPhone;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }
}
