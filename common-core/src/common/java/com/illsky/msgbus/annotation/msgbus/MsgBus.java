package com.illsky.msgbus.annotation.msgbus;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MsgBus {
    String sendSys();
    String clsName();
}
