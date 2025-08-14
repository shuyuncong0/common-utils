package com.illsky.msgbus.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @author: succongccong
 * @date: 2024-05-10
 * @description: TODO
 * @modiFy:
 */
public class KeyUtil {
    public static long generateUniqueSerialLong(String inputString) {
        String datePrefix = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        // 计算字符串的哈希码
        int hash = inputString.hashCode();
        long workerId = Math.abs(hash % 32);
        long datacenterId = Math.abs((hash / 32) % 32);
        // 创建Snowflake对象
        Snowflake snowflake = IdUtil.getSnowflake(workerId, datacenterId);
        // 生成流水号
        String serialNumber = Long.toString(snowflake.nextId());
        // 截取流水号的后十位
        String finalSerialNumber = serialNumber.substring(serialNumber.length() - 10);
        // 组合日期和流水号
        String fullSerialNumber = datePrefix + finalSerialNumber;
        return Long.parseLong(fullSerialNumber);
    }
}
