package com.illsky.msgbus.handler;


import cn.hutool.core.util.ReflectUtil;
import com.illsky.msgbus.ConsumerFactory;
import com.illsky.msgbus.annotation.apilog.ApiLog;
import com.illsky.msgbus.annotation.msgbus.MsgType;
import com.illsky.msgbus.pojo.ResponseResult;
import com.illsky.msgbus.pojo.msgbus.MsgBusDTO;
import com.illsky.msgbus.service.IMsgBusProducerService;
import com.illsky.msgbus.service.IMsgBusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * @author: sucongcong
 * @date: 2022/11/30
 * @descripyion: 消息总线对外访问接口
 * @modify:
 */
@RestController
@RequestMapping("mis/msgbus")
@Slf4j
public class MisMsgBusController {

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    protected IMsgBusProducerService msgBusProducerService;

    /**
     * @author: sucongcong
     * @date: 2023/2/3
     * @param msgBusDTO
     * @return com.topscomm.pub.vo.ResponseResult
     * @description: 消息处理
     * @modify:
     */
    @PostMapping("/handler")
    @ApiLog
    public ResponseResult<Object> hander(@RequestBody @Validated MsgBusDTO msgBusDTO) {
        try {
            // 获取服务类对象
            IMsgBusService msgBusService = ConsumerFactory.getMsgBusService(msgBusDTO.getSendSys(), msgBusDTO.getClsName());
            if (msgBusService == null) {
                log.info("发送系统：{},业务对象：{},无处理服务类！", msgBusDTO.getSendSys(), msgBusDTO.getClsName());
                return ResponseResult.error("未找到对应的服务！");
            }
            Method msgTypeMethod = null;
            Method[] methods = msgBusService.getClass().getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(MsgType.class)) {
                    MsgType msgType = method.getAnnotation(MsgType.class);
                    if (msgType.msgType().equals(msgBusDTO.getMsgType())) {
                        msgTypeMethod = method;
                    }
                }
            }
            if (msgTypeMethod == null) {
                msgTypeMethod = ReflectUtil.getMethod(msgBusService.getClass(), msgBusDTO.getMsgType(), MsgBusDTO.class);
            }
            if (msgTypeMethod == null) {
                log.info("发送系统：{},业务对象：{},消息类型:{},无处理方法！", msgBusDTO.getSendSys(), msgBusDTO.getClsName(), msgBusDTO.getMsgType());
                return ResponseResult.error("未找到对应的方法！");
            }
            try {
                // 反射调用消息类型方法
                return ReflectUtil.invoke(msgBusService, msgTypeMethod, msgBusDTO);
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if (cause != null) {
                    // 抛出真实的异常
                    throw cause.getCause() != null ? cause.getCause() : cause;
                } else {
                    // 如果没有内部原因，抛出原始异常
                    throw e;
                }
            }
        } catch (RuntimeException e) {
            // 如果是自定义异常，直接抛出 自定义子异常
            log.info("MessageException异常，方法处理失败，{}", e.getMessage());
            return ResponseResult.error(e.getMessage());
        } catch (Exception e) {
            // 如果是自定义异常，直接抛出 自定义上级异常
            log.info("ServiceException异常，方法处理失败，{}", e.getMessage());
            return ResponseResult.error(e.getMessage());
        } catch (Throwable ee) {
            log.error("消息处理异常", ee);
            return ResponseResult.error("消息处理异常！");
        }
    }


    /**
     * 推送消息
     *
     * @return
     * @author sucongcong
     * @since 2024-05-10 15:34
     */
    @PostMapping("/pushMsg")
    @ApiLog
    public ResponseResult<Object> pushMsg() {
        msgBusProducerService.pushMsg("clsName","msgType",new ArrayList<>());
        return ResponseResult.ok("");
    }

}
