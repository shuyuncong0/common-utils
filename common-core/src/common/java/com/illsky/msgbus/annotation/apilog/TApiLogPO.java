package com.illsky.msgbus.annotation.apilog;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 接口调用日志表
 * </p>
 *
 * @author
 * @since 2025-09-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TApiLogPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 来源系统
     */
    private String sourceSystem;

    /**
     * 调用链ID/唯一追踪ID
     */
    private String traceId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 请求唯一标识（可和调用方对应）
     */
    private String requestId;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 操作描述（注解value）
     */
    private String operation;

    /**
     * 请求URI
     */
    private String requestUri;

    /**
     * HTTP方法
     */
    private String httpMethod;

    /**
     * 请求来源IP
     */
    private String clientIp;

    /**
     * 请求参数(JSON)
     */
    private String requestParams;

    /**
     * 请求体(JSON)
     */
    private String requestBody;

    /**
     * 响应体(JSON)
     */
    private String responseBody;

    /**
     * 是否成功 1=成功 0=失败
     */
    private Boolean success;

    /**
     * 异常信息
     */
    private String errorMessage;

    /**
     * 执行时间(毫秒)
     */
    private Long executionTime;

    /**
     * 创建时间
     */
    private Date createTime;


}
