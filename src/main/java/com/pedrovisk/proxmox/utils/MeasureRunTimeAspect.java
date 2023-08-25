package com.pedrovisk.proxmox.utils;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MeasureRunTimeAspect {

    @Around("@annotation(MeasureRunTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long initTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - initTime;
        System.out.println("============================================================================================================");
        System.out.println(joinPoint.toShortString() + " executed in " + executionTime + "ms");
        System.out.println("============================================================================================================");
        return proceed;
    }

}
