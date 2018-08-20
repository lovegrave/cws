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

    /**
     * 起始页
     */
    private Integer currentPage = 1;
    /**
     * 每页的条数
     */
    private Integer pageSize = 20;
    /**
     * 查询的方式,默认为
     */
    private Integer style;


}
