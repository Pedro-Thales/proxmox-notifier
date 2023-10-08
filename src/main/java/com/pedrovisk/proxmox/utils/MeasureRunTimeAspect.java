package com.pedrovisk.proxmox.utils;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MeasureRunTimeAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureRunTimeAspect.class);

    @Around("@annotation(MeasureRunTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long initTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - initTime;
        LOGGER.info("============================================================================================================");
        LOGGER.info(joinPoint.toShortString() + " executed in " + executionTime + "ms");
        LOGGER.info("============================================================================================================");
        return proceed;
    }

}
