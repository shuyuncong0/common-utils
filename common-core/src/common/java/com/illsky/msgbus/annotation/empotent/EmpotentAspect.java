package com.illsky.msgbus.annotation.empotent;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucongcong
 * @date: 2024-07-30
 * @description: 保证方法执行幂等
 * @modify:
 */
@Aspect
@Component
@Slf4j
public class EmpotentAspect implements Ordered {

    // 流水号条形码生成策略
    public static final String GENECODE_PREFIX = "LCCX:SN:GENECODE:";

    /**
     * redis缓存key的模板
     */
    private static final String KEY_TEMPLATE = GENECODE_PREFIX + "%s";
    // 最大尝试次数
    private static final int MAX_RETRIES = 10;
    // 线性退避策略
    private static final long RETRY_INTERVAL = 200;
    @Resource
    private RedisTemplate<String,String> redisTemplate;


    // 切入点
    @Pointcut(value = "@annotation(com.illsky.msgbus.annotation.empotent.MsgEmpotent)")
    public void executeIdempotent() {
    }

    @Around("executeIdempotent()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取幂等注解
        MsgEmpotent idempotent = method.getAnnotation(MsgEmpotent.class);
        String value = UUID.randomUUID().toString().replace("-","");
        //根据 key前缀 + @Idempotent.value() + 方法签名 + 参数 构建缓存键值
        //确保幂等处理的操作对象是：同样的 @Idempotent.value() + 方法签名 + 参数
        String sysKey = "";
        // 默认方法
        if (StrUtil.isBlank(idempotent.value())) {
            // 全类名
            String className = joinPoint.getTarget().getClass().getName();
            // 方法名
            String methodName = method.getName();
            sysKey = className + "_" + methodName;
        } else {
            sysKey = idempotent.value();
        }
        //调用KeyUtil工具类生成key
        String key = String.format(KEY_TEMPLATE, sysKey );

        int retryCount = 0;
        Boolean acquire = null;
        while (retryCount < MAX_RETRIES) {
             acquire = redisTemplate.opsForValue().setIfAbsent(key, value, idempotent.expireSeconds(), TimeUnit.SECONDS);
            if(Boolean.FALSE.equals(acquire)){
                retryCount++;
                try {
                    // 采用简单的线性退避策略
                    Thread.sleep(RETRY_INTERVAL * retryCount);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted", e);
                }
            } else {
                break;
            }
        }

        /*if(Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, idempotent.expireSeconds(), TimeUnit.SECONDS))){
            throw new LccxException("系统正在处理，请稍后再试");
        }*/
        if(Boolean.FALSE.equals(acquire)) {
            throw new RuntimeException("系统正在处理，请稍后再试");
        }
        try {
            return joinPoint.proceed();
        } finally {
            // if(redisTemplate.opsForValue().get(key)!=null && Objects.equals(redisTemplate.opsForValue().get(key), value)){
            // }
            redisTemplate.delete(key);
        }
    }


    @Override
    public int getOrder() {
        // 或其他比事务优先级高的值, 避免出现释放锁但未提交的情况
        return Ordered.HIGHEST_PRECEDENCE;
    }


}
