/**
 * All Rights Reserved , Copyright (C) 2021 , 青岛鼎信通讯股份有限公司
 *
 * IEhrResignService
 * 离职申请单
 *
 * 修改纪录
 * 2021-08-31 版本：1.0 wanggongming 创建。
 * @version 版本：1.0
 * @author 作者：wanggongming
 * 创建日期：2021-08-31
 * 人力离职申请单
 */

package com.illsky.msgbus.service;

import com.illsky.msgbus.pojo.ResponseResult;

import java.util.List;
public interface IMsgBusProducerService {


    /**
     * @author: succ
     * @date: 2022/5/11
     * @param clsName 业务对象
     * @param msgType 消息类型
     * @param map data数据（单个） {@link Object}
     * @return: ResponseResult
     * @description: 消息总线发送服务
     * @modify:
     */
    ResponseResult<Object> pushMsg(String clsName,String msgType,Object map);


    /**
     * @author: succ
     * @date: 2022/5/11
     * @param clsName 业务对象
     * @param msgType 消息类型
     * @param list data数据（集合） {@link List<?>}
     * @description: 消息总线发送服务
     * @modify:
     */
    ResponseResult<Object> pushMsg(String clsName, String msgType, List<?> list);


}
