package com.illsky.msgbus.annotation.apilog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: sucongcong
 * @date: 2023/2/3
 * @descripyion: 记录接口日志
 * @modify:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLog {
}
