package cn.yanss.m.kitchen.cws.anntate;

import java.lang.annotation.*;

/**
 * 限流
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public  @interface ServiceLimit {
    String description()  default "";
}
