package cn.yanss.m.kitchen.cws.entity.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单所有参数
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse implements Serializable {

    private Integer oid;
    /**
     * 取货号
     */
    private String orderPick;
    /**
     * 重试机制，次数
     */
    private Integer times = 0;
    /**
     * 订单流程状态 1、已付款 2、已接单 3、配送中 4、已完成 5、配送回调异常/手动处理异常 6、厨房端操作异常 7、已退款 99、已取消
     */
    private Integer totalStatus;
    /**
     * 区别与totalStatus,它为totalStatus存入缓存之前的状态,存入缓存后,该状态改变
     */
    private Integer beforeStatus;
    /**
     * 订单的异常状态
     */
    private Integer exceptionStatus;
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 订单流水号
     */
    private String orderNo;
    /**
     *
     */
    private Integer orderType;
    /**
     * 订单创建时间
     */
    private Date createTime;
    /**
     * 商品价格
     */
    private double productPrice;
    /**
     *优惠金额
     */
    private double couponPrice;
    /**
     * 配送费
     */
    private double shipPrice;
    /**
     * 包装费
     */
    private double boxPrice;
    /**
     * 订单总价格
     */
    private double orderPrice;
    /**
     * 打包时间
     */
    private Date packUserTime;
    /**
     * 支付时间
     */
    private Date payTime;
    /**
     * 收货人姓名
     */
    private String receiverName;
    /**
     * 收货人电话
     */
    private String receiverPhone;
    /**
     * 收货人地址
     */
    private String receiverAddress;
    /**
     * 收货人经度
     */
    private double receiverLng;
    /**
     * 收货人维度
     */
    private double receiverLat;
    /**
     * 收货人性别
     */
    private String receiverSex;
    /**
     * 预计收货时间
     */
    private Date serviceTime;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 配送状态
     */
    private Integer sendStatus;
    /**
     * 退款状态
     */
    private Integer refundStatus;
    private Integer taskUserId;
    /**
     * 配送员姓名
     */
    private String taskUserName;
    /**
     * 配送员电话
     */
    private String taskUserPhone;
    /**
     * 取货时间
     */
    private Date taskUserTime;
//    /**
//     * 配送员经度
//     */
//    private double taskOrderLng;
//    /**
//     * 配送员维度
//     */
//    private double taskOrderLat;
    /**
     * 完成时间
     */
    private Date taskTime;
    /**
     * 配送公司
     */
    private String sendName;
    /**
     * 美团配送id
     */
    private String mtPeisongId;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 配送异常时间
     */
    private Date sendExceptionTime;
    private Date serviceExceptionTime;
    /**
     * 商品
     */
    private List<ProductResponse> productInfo;
    /**
     * 取消类型
     */
    private String cancelType;
    /**
     * 取消备注
     */
    private String cancelRemark;
    /**
     * 用餐人数
     */
    private Integer dinnerCount;
    /**
     *
     */
//    private String status;
    /**
     * 店铺id
     */
    private Integer storeId;
    /**
     * 美团配送
     */
    private Long deliveryId;
    /**
     * 异常备注
     */
    private String exceptionRemark;
    /**
     * 预定类型
     */
    private Integer bookingType;
    /**
     * 取消代码 0.正常 1.取消中 2.审核完成 3.取消完成 4.取消失败
     */
    private Integer cancelCode = 0;
    /**
     * 店铺负责人电话
     */
    private String linkman;
    /**
     * 订单备注
     */
    private String memberRemark;

}
