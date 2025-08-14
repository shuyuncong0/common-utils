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
package com.illsky.msgbus.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.illsky.msgbus.MsgBusPushFacade;
import com.illsky.msgbus.pojo.ResponseResult;
import com.illsky.msgbus.service.IMsgBusProducerService;
import com.illsky.msgbus.util.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "msgBusProducerService")
@Scope(value = "singleton")
public class MsgBusProducerService implements IMsgBusProducerService {


    protected final static Logger logger = LoggerFactory.getLogger(MsgBusProducerService.class);

    // @Autowired
    // public IMisApiLogService misApiLogService;
    // @Resource(name = "misMqMessageSendLogService")
    // private IMisMqMessageSendLogService misMqMessageSendLogService;

    @Autowired
    public MsgBusPushFacade msgBusPushFacade;

    /**
     * @author: sucongcong
     * @date: 2022/12/20
     * @param clsName 业务对象
     * @param msgType 消息类型
     * @param map data数据（单个） {@link Object}
     * @return com.topscomm.pub.vo.ResponseResult
     * @description: 消息总线推送通用接口
     * @modify:
     */
    @Override
    public ResponseResult pushMsg(String clsName, String msgType, Object map) {
        List<Object> list = new ArrayList<>();
        list.add(map);
        return this.pushMsg(clsName,msgType,list);
    }

    /**
     * @author: sucongcong
     * @date: 2022/12/20
     * @param clsName 业务对象
     * @param msgType 消息类型
     * @param list data数据（集合） {@link List<?>}
     * @return com.topscomm.pub.vo.ResponseResult
     * @description: 消息总线推送通用接口
     * @modify:
     */
    @Override
    public ResponseResult<Object> pushMsg(String clsName, String msgType, List<?> list){
        //下发开关判断
        // if (!ParameterCache.getBooleanValue("MisMsgBusEvent",false)) {
        //     return ResponseResult.ok("下发开关MisMsgBusEvent未开启");
        // }
        //获取系统编号
        //Config.getInstance().getValue("MisMsgBusCode","");
        String systemCode = "" ;
        //获取系统事件id.记录日志，随机唯一ID
        long msgId = KeyUtil.generateUniqueSerialLong(systemCode + "Event");
        JSONArray jsonArr = new JSONArray(list);
        String message = getMessage(systemCode,clsName,msgType,msgId,  jsonArr);
        ResponseResult<Object> responseResult;
        try {
            responseResult = msgBusPushFacade.pushMsgBus(systemCode,message);
        }catch (Exception e){
            logger.warn("推送消息代理异常：{}",e.getMessage());
            responseResult = ResponseResult.error("推送消息失败！");
        }
        sendLog(message, responseResult.toString());
        return responseResult;
    }



    /**
     * @author: mwf
     * @date: 2023年2月16日19:49:02
     * @param message 发送内容
     * @param result 发送结果
     * @description: 记录发送日志
     * @modify:
     */
    private void sendLog(String message, String result){
       /* if (!ParameterCache.getBooleanValue("MisMsgSendLogFlage",false)) {
            return ;
        }
        MisMqMessageSendLogEntity logEntity=new MisMqMessageSendLogEntity();
        logEntity.setTablename("MsgBus");
        logEntity.setSendlog(message.length()>1200?message.substring(0, 1200):message);
        logEntity.setDescription(result.length()>300?result.substring(0, 300):result);
        misMqMessageSendLogService.insert(logEntity);*/
    }

    /**
     * @author: mwf
     * @date: 2023年2月16日19:49:02
     * @param system 系统编号
     * @param soureType 来源对象
     * @param msgType 来源ID
     * @param dataJson  事件数据信息
     * @description: 组组装报文
     * @modify:
     */
    private String getMessage(String system, String soureType, String msgType, long msgId, JSONArray dataJson) {
        //组装事件的报文数据
        JSONObject jsonMQData = new JSONObject();
        /*jsonMQData.put(MisSystemConst.MsgBusConfig.MSGID , msgId);
        jsonMQData.put(MisSystemConst.MsgBusConfig.SENDSYS , system);
        jsonMQData.put(MisSystemConst.MsgBusConfig.CLSNAME , soureType);
        jsonMQData.put(MisSystemConst.MsgBusConfig.MSGTYPE , msgType);
        jsonMQData.put(MisSystemConst.MsgBusConfig.VER ,"1.0");
        jsonMQData.put(MisSystemConst.MsgBusConfig.HEADERDATA ,"");
        jsonMQData.put(MisSystemConst.MsgBusConfig.PREMSG ,"");
        jsonMQData.put(MisSystemConst.MsgBusConfig.DATA , dataJson);*/
        return jsonMQData.toString();
    }


}
