package com.illsky.msgbus.annotation.empotent;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MsgEmpotent {
    /**
     * 幂等名称，作为redis缓存Key的一部分。
     */
    String value() default "";

    /**
     * 幂等过期时间，即：在此时间段内，对API进行幂等处理。默认一小时
     */
    long expireSeconds() default 3600;
}
