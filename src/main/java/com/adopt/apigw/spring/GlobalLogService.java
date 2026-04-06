package com.adopt.apigw.spring;

import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class GlobalLogService {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalLogService.class);
    
    @Around("execution(* com.adopt.apigw.service..*.* (..))")
    public Object logBeforeAndAfterServiceMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    	
//        LOGGER.info("{} has started execution.", proceedingJoinPoint.getSignature());
        
        Object resultOfMethodCall = proceedingJoinPoint.proceed();
        
//        LOGGER.info("{} finished execution", proceedingJoinPoint.getSignature());
        
        return resultOfMethodCall;
    }
}
