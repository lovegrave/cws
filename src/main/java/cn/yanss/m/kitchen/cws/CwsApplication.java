package cn.yanss.m.kitchen.cws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CwsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CwsApplication.class, args);
    }
}
