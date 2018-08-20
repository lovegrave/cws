package cn.yanss.m.kitchen.cws.common;

/**
 * @author
 */
public class ReturnModel extends GenericReturnModel<Object> {

    public ReturnModel() {
        this.setTimestamp(System.currentTimeMillis());
        this.setCode(200);
        this.setMessage("success");
        this.setError("success");

    }

    public ReturnModel(Integer code, String message) {
        this.setTimestamp(System.currentTimeMillis());
        this.setCode(code);
        this.setMessage(message);
    }

    public ReturnModel(String code, String message) {
        this.setTimestamp(System.currentTimeMillis());
        this.setCode(Integer.parseInt(code));
        this.setMessage(message);
    }

    public ReturnModel(String code, String message, Object data) {
        this.setTimestamp(System.currentTimeMillis());
        this.setCode(Integer.parseInt(code));
        this.setMessage(message);
        this.setData(data);

    }

    public ReturnModel(Integer code, String message, Object data) {
        this.setTimestamp(System.currentTimeMillis());
        this.setCode(code);
        this.setMessage(message);
        this.setData(data);
    }

//    public ReturnModel(ExceptionEnum en, Object data) {
//        this.setTimestamp(System.currentTimeMillis());
//        this.setCode(Integer.parseInt(en.getErrcode()));
//        this.setMessage(en.getErrmsg());
//        this.setData(data);
//    }

    public ReturnModel(Object data) {
        this.setTimestamp(System.currentTimeMillis());
        this.setCode(200);
        this.setMessage("success");
        this.setError("success");
        this.setData(data);
    }

//    public ReturnModel(ExceptionEnum en){
//        this.setTimestamp(System.currentTimeMillis());
//        this.setCode(Integer.parseInt(en.getErrcode()));
//        this.setMessage(en.getErrmsg());
//    }
//
//    public ReturnModel(MallException exception) {
//        this.setTimestamp(System.currentTimeMillis());
//        this.setCode(Integer.parseInt(exception.getErrcode()));
//        this.setMessage(exception.getErrmsg());
//        this.setData(exception.getData());
//    }

    public ReturnModel(Exception exception) {
        this.setTimestamp(System.currentTimeMillis());
        this.setCode(500);
        this.setMessage("error");
        this.setError("系统错误");
//        this.setErrcode(ExceptionEnum.UNKNOWN_ERROR.getErrcode());
//        this.setErrmsg(ExceptionEnum.UNKNOWN_ERROR.getErrmsg());

    }

}
