package com.illsky.retry.annotation;

import java.lang.annotation.*;

//定义一个写在方法上的注解
@Target(ElementType.METHOD)
//此注解会在class中存在，运行时可通过反射获取
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetryExceptionHandler {

}
