package cn.yanss.m.kitchen.cws.entity.request;

import lombok.Data;

@Data
public class OrderRequest {

    /**
     * 状态
     */
    private Integer status;
    /**
     * 店铺id
     */
    private Integer storeId;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 取货号
     */
    private String orderPick;
    /**
     * 请求token
     */
    private String token;


}
