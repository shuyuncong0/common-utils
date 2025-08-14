/**
 * All Rights Reserved , Copyright (C) 2022 , 青岛鼎信通讯股份有限公司
 *
 * msgBusProducerService
 * 消息总线
 *
 * 修改纪录
 * 2022/12/11 版本：1.0 sucongcong 创建。
 * @version 版本：1.0
 * @author 作者：sucongcong
 * 创建日期：2022/12/11
 */
package com.illsky.msgbus;

import cn.hutool.json.JSONUtil;
import com.illsky.msgbus.pojo.ResponseResult;
import com.illsky.msgbus.util.MsgBusUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


@Service(value = "msgBusPushFacade")
@Slf4j
@EnableRetry
public class MsgBusPushFacade {



    /**
     * @author: mwf
     * @date: 2023年2月16日19:49:02
     * @param system 发送系统
     * @param message 发送结果
     * @description: 记录发送日志
     * @modify:
     */
    @Retryable(value = Exception.class,maxAttempts = 3,backoff = @Backoff(delay = 1000,multiplier = 1.5))
    public ResponseResult<Object> pushMsgBus(String system, String message){
        String result = MsgBusUtil.sendMsgBusEvent(system,message);
        return JSONUtil.toBean(result, ResponseResult.class);
    }
}
