/** 
* All Rights Reserved , Copyright (C) 2023 , 青岛鼎信通讯股份有限公司
* IRetryLogService
* SIM卡接口传输日志
* 修改纪录
* 2023-04-22 版本：1.0 sucongcong 创建。
* @version 版本：1.0
* @author 作者：sucongcong
* 创建日期：2023-04-22
*/

package com.illsky.retry.service;

import com.baomidou.mybatisplus.service.IService;
import com.illsky.retry.pojo.RetryLogEntity;
import java.util.Date;
import java.util.List;

public interface IRetryLogService extends IService<RetryLogEntity> {

    /**
     * @author: sucongcong
     * @date: 2023/4/25
     * @param classname 全类名
     * @param methodname 方法名
     * @param responsemsg 响应信息
     * @param retryexpirydate 重试过期时间
     * @param digest 摘要哈希值
     * @param methodParamValues 方法参数值
     * @return com.topscomm.sim.pojo.RetryLogEntity
     * @description: 新增接口日志
     * @modify:
     */
    RetryLogEntity insertApiLog(String classname, String methodname, String responsemsg, Date retryexpirydate, String digest, Object... methodParamValues);

    /**
     * @author: sucongcong
     * @date: 2023/4/22
     * @param apiLogEntityList
     * @description: 执行补偿机制
     * @modify:
     */
    void handlerApiLog(List<RetryLogEntity> apiLogEntityList);

    /**
     * @author: sucongcong
     * @date: 2023/4/22
     * @param apiLogEntity
     * @description: 执行补偿机制
     * @modify:
     */
    void handlerApiLog(RetryLogEntity apiLogEntity);


		
}
