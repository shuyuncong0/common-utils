package com.illsky.msgbus.annotation.apilog;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义API日志记录注解
 * @author sucongcong
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiLog {
    /**
     * 接口功能描述
     */
    String description() default "";

    /**
     * 来源系统
     */
    String sourceSystem() default "";

    /**
     * 请求唯一标识, 支持SpEL表达式
     */
    String requestId() default "";

    /**
     * 业务类型，取代通过方法名猜测的方式
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 自定义日志级别
     */
    LogLevel level() default LogLevel.INFO;

    /**
     * 日志级别枚举
     */
    enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    /**
     * 业务类型枚举
     */
    enum BusinessType {
        CREATE,
        UPDATE,
        DELETE,
        QUERY,
        LOGIN,
        OTHER
    }
}