package com.illsky.retry.pub;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.illsky.retry.config.RetryConfig;
import com.illsky.retry.exception.RetryException;
import com.illsky.retry.pojo.RetryLogEntity;
import com.illsky.retry.service.IRetryLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * @author: succongccong
 * @date: 2023-05-17
 * @description: 切面类---用于实现切面的功能
 * @modiFy:
 */
@Service
@Aspect
@Slf4j
public class RetryExceptionAspect {

    @Resource
    private RetryConfig retryConfig;
    @Resource
    public IRetryLogService retryLogService;

    //定义第一个切面
    @Pointcut(value = "@annotation(com.illsky.retry.annotation.RetryExceptionHandler)")
    public void  pointcut(){}

    //若切面方法运行代码后有错误抛出，则进入此处进行操作
    @AfterThrowing(value = "pointcut()" , throwing = "e")
    public void afterThrowing(JoinPoint point, RetryException e){
        try {
            // 全局线程池异常处理
            ThreadUtil.execAsync(() -> {
                handlerException(point, e);
            });
            //可以打印响应结果信息
        } catch (Exception eee) {
            log.error(eee.getMessage());
            //系统异常处理逻辑
        }
    }

    /**
     * @author: sucongcong
     * @date: 2023/6/5
     * @param point
     * @param e
     * @return: void
     * @description: 日志记录
     * @modify:
     */
    private void handlerException(JoinPoint point, RetryException e) {

        // 使用异常获得service 和 方法
        // StackTraceElement stackTraceElement = ExceptionUtil.getRootCause(e).getStackTrace()[0];
        // String className = stackTraceElement.getClassName();
        // String methodName = stackTraceElement.getMethodName();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> targetClass = point.getTarget().getClass();
        // 全类名
        String className = targetClass.getName();
        // 方法名
        String methodName = method.getName();
        // 日志状态 0：未解决；1：已解决
        String status = "0";
//        newinstancename = Class.forName(className).getAnnotation(Service.class).value();
//        String name = AopUtils.getTargetClass(bean).getName();
        // 摘要 class_name+method_name+method_param_values
        String digest = DigestUtil.md5Hex(className+methodName+JSONObject.toJSONString(point.getArgs(), SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty));
        //查询是否存在未解决的接口日志记录
        List<RetryLogEntity> retryLogEntities = retryLogService.selectList(new EntityWrapper<RetryLogEntity>().eq(RetryLogEntity.FieldStatus, status).eq(RetryLogEntity.FieldDigest, digest));
        if (retryLogEntities != null && retryLogEntities.size() > 0) {
            RetryLogEntity oldRetryLogEntity = retryLogEntities.get(0);
            RetryLogEntity retryLogEntity = new RetryLogEntity();
            retryLogEntity.setRetrycount(oldRetryLogEntity.getRetrycount() + 1);
            retryLogEntity.setModifiedon(new Date());
            retryLogEntity.setStatus(0);
            retryLogEntity.setMaxretrycount(retryConfig.getMaxRetryCount());
            // 修改更新时间和重试次数
            retryLogService.update(retryLogEntity, new EntityWrapper<RetryLogEntity>().eq(RetryLogEntity.FieldId, oldRetryLogEntity.getId()));
        } else {
            retryLogService.insertApiLog(className, methodName, e.getRes(), e.getRetryexpirydate(), digest, point.getArgs());
        }

    }


}
