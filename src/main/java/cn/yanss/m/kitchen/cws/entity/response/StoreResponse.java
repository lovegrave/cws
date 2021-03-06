package cn.yanss.m.kitchen.cws.entity.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Data
public class StoreResponse {
    private Integer storeId;
    private String storeName;
    private String meituanStoreId;
    private String dadaStoreId;
    private String bardStoreId;
    private String storeIntro;
    private Double lng;
    private Double lat;
    private String linkman;
    private Date createTime;
    private Integer status;
    private java.sql.Time startTime;
    private java.sql.Time endTime;
    private String province;
    private String city;
    private String country;
    private String addressDetail;
    private Double sendPrice;
    private String location;
    private String pic;
    private String sendType;
    private Integer bookDay;
    private Integer receiveTime;
}
