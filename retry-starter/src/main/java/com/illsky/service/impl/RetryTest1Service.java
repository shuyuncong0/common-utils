/** 
* All Rights Reserved , Copyright (C) 2023 , 青岛鼎信通讯股份有限公司
* RetryLogService
* SIM卡接口传输日志
* 修改纪录
* 2023-04-22 版本：1.0 sucongcong 创建。
* @version 版本：1.0
* @author 作者：sucongcong
* 创建日期：2023-04-22
*/
package com.illsky.service.impl;


import com.illsky.retry.annotation.RetryExceptionHandler;
import com.illsky.retry.exception.RetryException;
import  com.illsky.service.IRetryTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(value = "singleton")
@Slf4j
public class RetryTest1Service  implements IRetryTestService {


    @Override
    @RetryExceptionHandler
    public void testString(String classname, String newinstancename) {
        if (1==1){
            throw new RetryException("123123");
        }
        System.out.println("testString");
    }
}
