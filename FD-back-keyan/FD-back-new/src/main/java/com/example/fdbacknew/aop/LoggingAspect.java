package com.example.fdbacknew.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Aspect
@Component
public class LoggingAspect {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 定义切点为所有Controller类中的方法
    @Pointcut("execution(* com.example.fdbacknew.controller.*.*(..))")
    public void controller() {
    }

    // 在方法执行前执行的通知
    @Before("controller()")
    public void logBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 获取并格式化当前时间
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);  // 使用定义好的格式化器格式化时间
        // 输出日志
        System.out.println("Access Time: " + formattedDateTime);
        System.out.println("Request URL: " + request.getRequestURL().toString());
        System.out.println("HTTP Method: " + request.getMethod());
        System.out.println("IP: " + request.getRemoteAddr());
        System.out.println("Class Method: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "controller()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("Method return value : " + result);
    }
}
