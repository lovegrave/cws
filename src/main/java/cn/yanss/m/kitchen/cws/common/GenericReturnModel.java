package cn.yanss.m.kitchen.cws.common;

/**
 * @param <T>
 * @author
 * @date 2017年2月15日 上午9:44:29
 */
public class GenericReturnModel<T> {
    /**
     * 状态编码
     */
    private Integer code;
    /**
     * 状态说明
     */
    private String message;
    /**
     * 结果集
     */
    private Object data;
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 错误返回
     */
    private String error;
    /**
     * 具体异常错误码
     */
    private String errcode;
    /**
     * 具体异常错误说明
     */
    private String errmsg;

    public Integer getCode() throws Exception{
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}