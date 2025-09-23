package com.sky.aspect;

import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;


@Aspect
@Component
@Slf4j
public class AutoFill {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void pt(){}
    @Before("pt()")
    public void AutoFill(JoinPoint joinPoint){
        log.info("自动填充");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        com.sky.annotation.AutoFill annotation = signature.getMethod().getAnnotation(com.sky.annotation.AutoFill.class);
        OperationType operationType = annotation.value();
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        Object[] args = joinPoint.getArgs();
        Object arg = args[0];
        try {
            arg.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class).invoke(arg, now);
            arg.getClass().getDeclaredMethod("setUpdateUser", Long.class).invoke(arg, currentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(operationType== OperationType.INSERT){
            try {
                arg.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class).invoke(arg, now);
                arg.getClass().getDeclaredMethod("setCreateUser", Long.class).invoke(arg, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
