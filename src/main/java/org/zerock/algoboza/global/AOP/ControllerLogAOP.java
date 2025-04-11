package org.zerock.arcteryx.global.AOP;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.zerock.arcteryx.global.JsonUtils;


@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class ControllerLogAOP {
    // 보기편하게 출력
    private final JsonUtils jsonUtils;


    // com.aop.controller 이하 패키지의 모든 클래스 이하 모든 메서드에 적용
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    private void controllerCut() {
    }

    // Pointcut에 의해 필터링된 경로로 들어오는 경우 메서드 호출 전에 적용
    @Before("controllerCut()")
    public void beforeLog(JoinPoint joinPoint) throws JsonProcessingException {
        // 실행 메소드 가져오기
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        log.info("Method = {}", methodSignature.getMethod().getName());

        // 파라미터 받아오기
        Object[] args = joinPoint.getArgs();
        if (args.length <= 0) log.info("no parameter");
        for (Object arg : args) {
            log.info("parameter value = {}", jsonUtils.toJson(arg));
        }
    }

    @AfterReturning(value = "controllerCut()", returning = "controllerReturnObj")
    public void afterLog(JoinPoint joinPoint, Object controllerReturnObj) throws JsonProcessingException {
        log.info("return value = {}", jsonUtils.toJson(controllerReturnObj));
    }
}

