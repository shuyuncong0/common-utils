package com.illsky.msgbus.pojo.msgbus;

import cn.hutool.json.JSONObject;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: sucongcong
 * @date: 2022/12/9
 * @descripyion: 消息代理服务传输参数
 * @modify:
 */
@Data
public class MsgBusDTO {

    /**
     * 消息Id
     */
    @NotNull(message = "消息Id不能为空")
    private String msgId;
    /**
     * 发送系统
     */
    @NotNull(message = "发送系统不能为空")
    private String sendSys;
    /**
     * 业务对象
     */
    @NotNull(message = "业务对象不能为空")
    private String clsName;
    /**
     * 消息类型
     */
    @NotNull(message = "消息类型不能为空")
    private String msgType;
    /**
     * 数据版本
     */
    @NotNull(message = "数据版本不能为空")
    private String ver;
    /**
     * 消息头部数据
     */
    private String headerData;
    /**
     * 前置消息
     */
    private String preMsg;

    /**
     * 明细列表
     */
    @NotNull(message = "列表不能为空")
    @Valid
    private List<JSONObject> data;
}
