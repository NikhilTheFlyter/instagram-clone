package com.instagram.auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.instagram.auth.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info(">>> Entering: {} with args: {}", methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        log.info("<<< Exiting: {} | returned: {} | duration: {}ms", methodName, result, duration);
        return result;
    }

    @AfterThrowing(pointcut = "execution(* com.instagram.auth.service..*(..))", throwing = "ex")
    public Object logServiceExceptions(JoinPoint joinPoint, Exception ex) {
        log.error("!!! Exception in {}: {} - {}",
                joinPoint.getSignature().toShortString(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
        return null;
    }
}
