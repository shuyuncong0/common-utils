package com.illsky.msgbus.annotation.methodlogger;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodLogger {

    String domain() default "";

    String tableName() default "";
}
