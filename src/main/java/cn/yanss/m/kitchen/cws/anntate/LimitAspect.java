package cn.yanss.m.kitchen.cws.anntate;

import com.google.common.util.concurrent.RateLimiter;
import common.returnModel.ReturnModel;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope
@Aspect
@Log4j2
public class LimitAspect {
    /**
     * 每秒只发出5个令牌，此处是单进程服务的限流,内部采用令牌捅算法实现
     */
    private static  RateLimiter rateLimiter = RateLimiter.create(5.0);

    /**
     * Service层切点  限流
     */
    @Pointcut("@annotation(cn.yanss.m.kitchen.cws.anntate.ServiceLimit)")
    public void ServiceAspect() {

    }

    @Around("ServiceAspect()")
    public  Object around(ProceedingJoinPoint joinPoint) {
        Boolean flag = rateLimiter.tryAcquire();
        Object obj = new ReturnModel();
        try {
            if(flag){
                obj = joinPoint.proceed();
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
        return obj;
    }
}
