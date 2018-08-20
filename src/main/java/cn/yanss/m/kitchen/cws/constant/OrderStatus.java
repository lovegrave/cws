package cn.yanss.m.kitchen.cws.constant;

public class OrderStatus {

    /**
     * 已付款
     */
    public static final int ACCOUNT_PAID = 1;
    /**
     * 以配送
     */
    public static final int INDEVELOPMENT = 2;
    /**
     * 配送中
     */
    public static final int DISTRIBUTION = 3;
    /**
     * 配送完成
     */
    public static final int DIS_COMPLETE = 4;
    /**
     * 配送异常
     */
    public static final int DELIVERY_ERROR = 5;
    /**
     * 未配送,厨房异常
     */
    public static final int KITCHEN_ERROR = 6;

    /**
     * 申请退款,订单取消
     */
    public static final int REFUND_ORDERS = 7;
    /**
     * 配送完成
     */
    public static final int SEND_COMPLETE= 8;
    /**
     * 订单异常
     */
    public static final int ERROR_STATUS = 199;
    /**
     * 订单取消
     */
    public static final int CANCEL_ORDER = 99;
    /**
     * 退款中
     */
    public static final int REFUND_STATUS = 100;

    public final static String ORDER_NOT_EXISTS = "该订单不存在";
    /**
     * 订单redis存储键头
     */
    public static final String ORDERSTATUS = "orderStatus";
    /**
     * 店铺redis存储键头,推送存储键头
     */
    public static final String STOREKEY = "store";
    /**
     * 店铺键
     */
    public static final String STORERESPONSE = "storeResponse";
    /**
     * 订单发配送存储键头
     */
    public static final String FLOW = "flow";
    /**
     * 预取消订单存储键头
     */
    public static final String CANCEL = "cancel";
    /**
     * 取货号与订单号关联存储键头
     */
    public static final String ORDER_PICK = "pick";
    /**
     * 订单完成后,迁移订单到redis存储键头
     */
    public static final String COMPLETE = "complete";
    /**
     * 订单申请退款,订单取消存储键头
     */
    public static final String REFUND = "refund";

    public static final String SEND_STATUS = "send";

    public static final String EXCEPTION_STATUS = "exception";

    public static final String THREAD_ERREO = "线程池中发现异常，被中断";
}
