package com.illsky.msgbus.annotation.empotent;

import cn.hutool.core.util.StrUtil;

import cn.hutool.crypto.digest.DigestUtil;
import com.illsky.msgbus.pojo.msgbus.MsgBusDTO;
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
 *
 *
 * @author sucongcong
 * @since 2024-04-13 11:43
 */
@Aspect
@Component
@Slf4j
public class MisEmpotentAspect implements Ordered {

    /**
     * redis缓存key的模板
     */
    private static final String KEY_TEMPLATE = "MSGBUS:LOCK:%s";
    /**任务调度锁，每个消费者一个*/

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
        String sysKey = "";
        // 适配 MIS 通用方法
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof MsgBusDTO) {
                // 现在你可以访问 msgBusDTO 的方法和属性
                MsgBusDTO msgBusDTO = (MsgBusDTO) arg;
                sysKey = msgBusDTO.getSendSys() + ":" + msgBusDTO.getMsgId();
            }
        }
        // 适配 自定义key
        if (StrUtil.isBlank(sysKey)) {
            // 全类名
            String className = joinPoint.getTarget().getClass().getName();
            // 方法名
            String methodName = method.getName();
            String idempotentValue = idempotent.value();
            if (StrUtil.isBlank(idempotentValue)) {
                idempotentValue = className + "_" + methodName;
            }
            StringBuilder sb = new StringBuilder(method.toString());
            for (Object arg : args) {
                sb.append(arg.toString());
            }
            sysKey = idempotentValue + ":" + DigestUtil.md5Hex(sb.toString());
        }

        //根据 key前缀 + @Idempotent.value() + 方法签名 + 参数 构建缓存键值
        //确保幂等处理的操作对象是：同样的 @Idempotent.value() + 方法签名 + 参数
        //调用KeyUtil工具类生成key
        String key = String.format(KEY_TEMPLATE, sysKey );
        if(Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(key, value))){
            throw new Exception("系统正在处理，请稍后再试");
        } else {
            redisTemplate.expire(key, idempotent.expireSeconds(), TimeUnit.SECONDS);
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
