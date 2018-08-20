package cn.yanss.m.kitchen.cws.entity.cancel;

import lombok.Data;
import java.util.Date;

@Data
public class OrderInfoRequest {

    /**
     * 异常代码
     */
    private Integer exceptionStatus;

    /**
     * 状态
     */
    private Integer status;
    /**
     * 店铺id
     */
    private Integer storeId;
    /**
     * 当前页
     */
    private Integer currentPage;
    /**
     * 条数
     */
    private Integer pageSize;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 厨房操作（1.确认制作，2.制作完成 3.确认派送）
     */
    private Integer actionType;
    /**
     * 历史订单时的开始时间
     */
    private Date startTime;
    /**
     * 历史订单结束时间
     */
    private Date endTime;
    /**
     * 创建的订单配送id
     */
    private Long deliveryId;
    /**
     * 美团内部订单id
     */
    private String mtPeisongId;


}
