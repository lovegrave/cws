package cn.yanss.m.kitchen.cws.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    private DateUtil() {
    }
    public final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    public static Date getCurrentTime(){
        return new Date();
    }

    public static String getStartTime() {
        return simpleDateFormat.format(new Date());
    }

    public static String DateConvert(Date date) {
        return simpleDateFormat.format(date);
    }

    public static String DateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return simpleDateFormat.format(date);
    }
}
