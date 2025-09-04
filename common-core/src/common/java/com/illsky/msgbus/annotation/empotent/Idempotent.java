package com.illsky.msgbus.annotation.empotent;


import java.lang.annotation.*;


/**
 * @author: sucongcong
 * @date: 2025-09-04
 * @description: 保证方法幂等性
 * @modify:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等 Key 的前缀（不同业务可区分）
     */
    String prefix() default "idempotent";

    /**
     * 幂等 Key 的 SpEL 表达式，例如 "#request.id"，用于从方法参数中生成唯一键
     */
    /**
     * 幂等 Key，可以是 SpEL 表达式（以 # 或 T( 开头）如 "#request.id"用于从方法参数中生成唯一键或普通字符串
     * 如果是 SpEL 表达式，必须以 '#' 开头；否则视为普通字符串
     */
    String key() default "";

    /**
     * 幂等过期时间，即：在此时间段内，对API进行幂等处理。默认一小时
     */
    long expireSeconds() default 30;
}
