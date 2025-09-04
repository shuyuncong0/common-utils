package com.illsky.msgbus.annotation.empotent;

import cn.hutool.core.util.StrUtil;
import com.cosmo.emes.common.model.exceptions.LccxException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: sucongcong
 * @date: 2025-09-04
 * @description: 保证方法执行幂等
 * @modify:
 */
@Aspect
@Component
@Slf4j
public class IdempotentAspect implements Ordered {


    // 最大尝试次数
    private static final int MAX_RETRIES = 5;
    // 线性退避策略
    private static final long RETRY_INTERVAL = 200;
    @Resource
    private RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    // 切入点
    @Pointcut(value = "@annotation(com.cosmo.emes.common.tool.infra.Idempotent)")
    public void executeIdempotent() {
    }

    @Around("executeIdempotent()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取幂等注解
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        String value = UUID.randomUUID().toString().replace("-","");
        //根据 key前缀 + @Idempotent.key() + 方法签名 + 参数 构建缓存键值
        //确保幂等处理的操作对象是：同样的 @Idempotent.value() + 方法签名 + 参数
        String key = buildCacheKey(idempotent, joinPoint, method);

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
            throw new LccxException("系统正在处理，请稍后再试");
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

    /**
     * 构建缓存键
     */
    private String buildCacheKey(Idempotent idempotent, ProceedingJoinPoint joinPoint, Method method) {
        // 如果key值为空，使用全类名+方法名+参数hash
        // 如果key值不为空且为Spel表达式，如果使用SpEL表达式，
        // 如果key值不为空且不为Spel表达式，如果使用SpEL表达式，则使用key值

        // 幂等 Key 的前缀
        String prefix = idempotent.prefix();
        //幂等 Key 的前缀（不同业务可区分）
        String key = idempotent.key();
        // 方法名
        String methodName = method.getName();
        // 类名
        String simpleClassName = joinPoint.getTarget().getClass().getSimpleName();
        // 处理 key 值
        String cacheKey = "";
        if (StrUtil.isBlank(key)) {
            // 全类名
            //String className = joinPoint.getTarget().getClass().getName();
            //String className = method.getDeclaringClass().getName();
            // 如果没有指定 key，使用类名+方法名
            cacheKey = String.format("%s:%s:%s", simpleClassName, methodName, hashArgs(joinPoint.getArgs()));
        } else if (isSpEl(key)) {
            // 如果未明确指定，自动判断：以 '#' 开头的视为 SpEL 表达式
            cacheKey = String.format("%s:%s:%s", simpleClassName, methodName, parseSpEL(key, joinPoint.getArgs(), method));
        } else {
            // 如果明确指定，则使用指定的 key
            cacheKey = String.format("%s:%s:%s", simpleClassName, key, hashArgs(joinPoint.getArgs()));
        }

        return String.format("%s:%s", prefix, cacheKey);
    }
    /**
     * 解析 SpEL 表达式
     */
    private String parseSpEL(String expression, Object[] args, Method method) {
        if (expression == null || expression.isEmpty()) {
            // 默认方法名作为 Key
            return method.getName();
        }
        // 如果是 SpEL 表达式（以 # 或 T( 开头），则走 SpringEL 解析
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (expression.startsWith("#") || expression.startsWith("T(")) {
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }
        }
        // 解析表达式并确保结果不为 null
        Object result = parser.parseExpression(expression).getValue(context);
        // 处理可能的 null 结果
        if (result == null) {
            log.warn("SpEL表达式解析结果为null: {}, 将使用表达式字符串本身", expression);
            // 返回原始表达式字符串
            return expression;
        }
        // 确保返回字符串
        return result.toString();
    }

    private String hashArgs(Object[] args) {
        try {
            String json = objectMapper.writeValueAsString(args);
            return DigestUtils.sha256Hex(json);
            // 生成一个安全的幂等 key
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize args for AUTO key", e);
        }
    }
    private boolean isSpEl(String expression) {
        return expression.startsWith("#") || expression.startsWith("T(");
    }

}
