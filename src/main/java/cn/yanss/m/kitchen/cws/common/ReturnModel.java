package cn.yanss.m.kitchen.cws.common;

public class ReturnModel {
    private Integer code;
    private String message;
    private Integer errcode;
    private String errmsg;
    private Object data;

    public Integer getCode() {
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

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ReturnModel(Object data) {
        this.data = data;
        this.code=200;
        this.message="success";
    }

    public ReturnModel() {
        this.code=200;
        this.message="success";
    }

    public ReturnModel(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
