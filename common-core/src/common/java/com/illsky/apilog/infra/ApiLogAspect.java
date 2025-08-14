package com.illsky.apilog.infra;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.util.StringUtils;
import com.illsky.apilog.pojo.MApiLogPO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author shuyuncong
 */
@Aspect
@Slf4j
@Component
public class ApiLogAspect {


    // 切入点
    @Pointcut(value = "@annotation(com.illsky.apilog.infra.ApiLog)")
    private void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        Object returnValue = null;
        String errorMsg = "";
        try {
            returnValue = point.proceed(point.getArgs());
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }


        // 使用异常获得service 和 方法
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
        // FastJSON
        //String digest = DigestUtil.md5Hex(className + methodName + JSONObject.toJSONString(point.getArgs(), SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty));
        // Hutool
        String digest = DigestUtil.md5Hex(className + methodName + JSONUtil.toJsonStr(point.getArgs(), JSONConfig.create().setIgnoreNullValue(false)));
        MApiLogPO apiLog = MApiLogPO.builder()
                .digest(digest)
                .className(className)
                .methodName(methodName)
                // FastJSON
                //.paramValues(JSON.toJSONString(point.getArgs(), SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty))
                .retryCount(0L)
                // FastJSON
                //.responseMsg(returnValue == null ? "" : JSON.toJSONString(returnValue, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty))
                // Hutool
                .responseMsg((returnValue == null) ? "" : JSONUtil.toJsonStr(returnValue, JSONConfig.create().setIgnoreNullValue(false)))
                .documentDate(LocalDateTime.now())
                .status(0)
                .errorMsg(errorMsg)
                .build();
        if (StringUtils.isNotBlank(errorMsg)) {
            log.error("接口调用异常：{}", errorMsg);
            throw new RuntimeException("接口调用异常："+errorMsg);
        }
        return returnValue;
    }


}
