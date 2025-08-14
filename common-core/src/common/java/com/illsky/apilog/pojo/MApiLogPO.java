package com.illsky.apilog.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备调用日志对象 m_api_log
 *
 * @author gen
 * @date 2025-07-08
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class MApiLogPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 哈希值	class_name+method_name+method_param_values */
    @ExcelProperty(value = "哈希值	class_name+method_name+method_param_values")
    private String digest;

    /** 接口地址 */
    @ExcelProperty(value = "接口地址")
    private String url;

    /** 单据类型 */
    @ExcelProperty(value = "单据类型")
    private String sourceType;

    /** 全类名 */
    @ExcelProperty(value = "全类名")
    private String className;

    /** 方法名 */
    @ExcelProperty(value = "方法名")
    private String methodName;

    /** 方法入参 */
    @ExcelProperty(value = "方法入参")
    private String paramValues;

    /** 重试次数 */
    @ExcelProperty(value = "重试次数")
    private Long retryCount;

    /** 最大重试次数 */
    @ExcelProperty(value = "最大重试次数")
    private Long maxRetryCount;

    /** 返回值 */
    @ExcelProperty(value = "返回值")
    private String responseMsg;

    /** 错误码 */
    @ExcelProperty(value = "错误码")
    private String errorCode;

    /** 错误信息 */
    @ExcelProperty(value = "错误信息")
    private String errorMsg;

    /** 单据日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "单据日期")
    private LocalDateTime documentDate;

    /** 处理状态 0：未解决；1：已解决 */
    @ExcelProperty(value = "处理状态 0：未解决；1：已解决")
    private Integer status;

    /** 备注 */
    @ExcelProperty(value = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
