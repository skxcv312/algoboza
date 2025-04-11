package org.zerock.algoboza.global.AOP;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.zerock.algoboza.global.JsonUtils;

@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class ServiceLogAOP {
    private final JsonUtils jsonUtils;

    // 나중에 특정 어노테이션만 가능하도록 해도 괜찮을듯
    @Pointcut("@within(org.springframework.stereotype.Service)")
    private void serviceCut() {
    }

//    @Before("serviceCut()")
//    public void beforeLog(JoinPoint joinPoint) {
//        // 실행 메소드 가져오기
//        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
//        log.info("Method = {}", methodSignature.getMethod().getName());
//
//        // 파라미터 받아오기
//        Object[] args = joinPoint.getArgs();
//        if (args.length <= 0) log.info("no parameter");
//        for (Object arg : args) {
//            log.info("parameter value = {}", customGson.gson().toJson(arg));
//        }
//    }

    @AfterReturning(value = "serviceCut()", returning = "serviceReturnObj")
    public void afterLog(JoinPoint joinPoint, Object serviceReturnObj) {
        // 실행 메소드 가져오기
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        log.info("Method = {}", methodSignature.getMethod().getName());
        log.info("return value = {}", jsonUtils.toJson(serviceReturnObj));
    }
}
