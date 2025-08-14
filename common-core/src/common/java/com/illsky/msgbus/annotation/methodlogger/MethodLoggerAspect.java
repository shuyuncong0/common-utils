package com.illsky.msgbus.annotation.methodlogger;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.illsky.basic.exception.ExternalInterfaceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *
 *
 * @author sucongcong
 * @since 2024-04-13 11:43
 */
@Aspect
@Component
@Slf4j
public class MethodLoggerAspect  {


    // @Resource
    // private MisApiLogService misApiLogService;

    // 切入点
    @Pointcut(value = "@annotation(com.illsky.msgbus.annotation.methodlogger.MethodLogger)")
    public void executeMethodLogger() {
    }

    @Around("executeMethodLogger()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取幂等注解
        MethodLogger methodLogger = method.getAnnotation(MethodLogger.class);
        // 全类名
        String className = joinPoint.getTarget().getClass().getName();
        // 方法名
        String methodName = method.getName();
        // 参数
        String argsStr = Arrays.toString(joinPoint.getArgs());
        // 记录返回值
        Object result = null;
        String resultStr = "服务名【" + className + "】-方法名【" + methodName + "】:" ;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
        } catch (ExternalInterfaceException e) {
            log.error(e.getMessage(), e);
            resultStr += Convert.toStr(e.getMessage());
            String errorInfo = "三方接口调用：" + resultStr + e.getMessage();
            // 记录并发送主动抛出的异常
            // ErrorLogUtil.writeLogAndSendMessage(new ExternalInterfaceException(errorInfo));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resultStr += Convert.toStr(e.getMessage());
            // 记录并发送主动抛出的异常
            // ErrorLogUtil.writeLogAndSendMessage(e);
        } catch (Throwable error) {
            log.error(error.getMessage(), error);
            resultStr += Convert.toStr(error.getMessage());
            // 未识别的异常只记录，不发送企业微信通知
            // ErrorLogUtil.writeLog(error);
        }
        if (StrUtil.isBlank(resultStr)) {
            resultStr = Convert.toStr(result);
        }
        // 保存apilog日志
        // misApiLogService.writeLog(methodLogger.domain(), methodLogger.tableName(), className, methodName, argsStr, resultStr);
        return result;
    }

}
