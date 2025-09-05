package com.illsky.msgbus.annotation.apilog;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * API日志记录切面
 * @author sucongcong
 */
@Aspect
@Component
@Slf4j
public class ApiLogAspect {

    @Resource(name = "apiLogTaskThreadPool")
    private ExecutorService apiLogTaskThreadPool;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();


    // 定义切点, 拦截被@ApiLog注解的类或方法
    @Pointcut("@annotation(com.illsky.msgbus.annotation.apilog.ApiLog) || @within(com.illsky.msgbus.annotation.apilog.ApiLog)")
    public void apiLogPointcut() {
    }

    // 环绕通知
    @Around("apiLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 获取实际的注解配置（优先使用方法上的注解）
        ApiLog apiLog = getActualAnnotation(joinPoint);
        // 没有注解直接执行原方法
        if (apiLog == null) {
            return joinPoint.proceed();
        }

        // 构建日志对象
        TApiLogPO logEntity = TApiLogPO.builder()
                .createTime(new Date())
                .build();

        // 优先记录基本信息和请求ID，即使在非Web环境下也应尽可能记录
        recordBasicInfo(logEntity, apiLog, joinPoint);

        Object result = null;
        try {
            // 在执行目标方法前记录请求信息
            recordRequestInfo(logEntity, joinPoint);
            result = joinPoint.proceed();
            logEntity.setSuccess(true);
            // 记录响应体
            if (result != null) {
                try {
                    logEntity.setResponseBody(objectMapper.writeValueAsString(result));
                } catch (Exception e) {
                    logEntity.setResponseBody("Response body serialization failed: " + e.getMessage());
                }
            }
            return result;
        } catch (Throwable e) {
            logEntity.setSuccess(false);
            logEntity.setErrorMessage(getExceptionInfo(e));
            // 必须重新抛出异常，以确保事务回滚等操作正常进行
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            logEntity.setExecutionTime(executionTime);
            // 异步保存日志
            saveLogAsync(logEntity);
            // 根据日志级别在控制台输出摘要
            logByLevel(apiLog.level(), buildLogMessage(logEntity));
        }
    }
    /**
     * 设置基本信息
     */
    private void recordBasicInfo(TApiLogPO logEntity, ApiLog apiLog, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 设置类名和方法名
        logEntity.setClassName(method.getDeclaringClass().getName());
        logEntity.setMethodName(method.getName());

        // 设置注解配置的信息
        logEntity.setSourceSystem(apiLog.sourceSystem());
        logEntity.setOperation(apiLog.description());

        // 使用注解中定义的业务类型，而不是通过方法名猜测
        logEntity.setBusinessType(apiLog.businessType().name());

        // 解析请求ID
        if (apiLog.requestId() != null && !apiLog.requestId().isEmpty()) {
            logEntity.setRequestId(parseSpEl(apiLog.requestId(), joinPoint.getArgs(), method));
        }
    }

    private void recordRequestInfo(TApiLogPO logEntity, ProceedingJoinPoint joinPoint) {
        // 检查当前是否为Web环境，避免在非Web环境下抛出异常
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            logEntity.setHttpMethod(request.getMethod());
            logEntity.setRequestUri(request.getRequestURI());
            logEntity.setClientIp(getClientIp(request));

            String traceId = request.getHeader("X-Trace-Id");
            if (traceId != null && !traceId.isEmpty()) {
                logEntity.setTraceId(traceId);
            }
        }

        // 如果经过上述步骤仍然没有追踪ID，则生成一个
        if (logEntity.getTraceId() == null || logEntity.getTraceId().isEmpty()) {
            logEntity.setTraceId(UUID.randomUUID().toString().replace("-", ""));
        }

        // 记录请求参数
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                logEntity.setRequestParams(objectMapper.writeValueAsString(args));

                if (requestAttributes instanceof ServletRequestAttributes) {
                    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                    String contentType = request.getContentType();
                    if (contentType != null && contentType.contains("application/json")) {
                        logEntity.setRequestBody(objectMapper.writeValueAsString(args[0]));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("记录请求参数/请求体失败: {}", e.getMessage());
            logEntity.setRequestParams("Request parameters serialization failed: " + e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于多个IP的情况（如X-Forwarded-For: client, proxy1, proxy2），取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 获取异常信息
     */
    private String getExceptionInfo(Throwable e) {
        StringBuilder sb = new StringBuilder();
        // 使用 e.toString() 获取更紧凑的异常信息头
        sb.append(e.toString());
        StackTraceElement[] stackTrace = e.getStackTrace();
        // 记录前5行堆栈以提供更多上下文
        int maxLines = Math.min(5, stackTrace.length);
        for (int i = 0; i < maxLines; i++) {
            sb.append("\n\tat ").append(stackTrace[i]);
        }
        if (stackTrace.length > maxLines) {
            sb.append("\n\t... ").append(stackTrace.length - maxLines).append(" more");
        }
        return sb.toString();
    }

    /**
     * 异步保存日志
     */
    public void saveLogAsync(TApiLogPO logEntity) {
        try {
            CompletableFuture.runAsync(() -> {
                try {
                    //createApiLog(logEntity);
                } catch (Exception e) {
                    log.warn("异步保存API日志失败 (feign call failed): {}", e.getMessage());
                }
            }, apiLogTaskThreadPool).exceptionally(ex -> {
                log.error("异步保存API日志任务提交失败: {}", ex.getMessage());
                return null;
            });
        } catch (Exception e) {
            log.error("提交异步日志保存任务时发生异常: {}", e.getMessage());
        }
    }

    /**
     * 根据日志级别输出日志
     */
    private void logByLevel(ApiLog.LogLevel level, String message) {
        switch (level) {
            case DEBUG: log.debug(message); break;
            case WARN: log.warn(message); break;
            case ERROR: log.error(message); break;
            default: log.info(message); break;
        }
    }

    /**
     * 构建日志消息
     */
    private String buildLogMessage(TApiLogPO logEntity) {
        String errorSummary = logEntity.getSuccess() ? "N/A" : (logEntity.getErrorMessage() != null ? logEntity.getErrorMessage().split("\n")[0] : "");
        return String.format("API日志 - 操作: %s, 来源系统: %s，业务类型: %s, 耗时: %dms, 结果: %s, 错误: %s",
                logEntity.getOperation(),
                logEntity.getSourceSystem(),
                logEntity.getBusinessType(),
                logEntity.getExecutionTime(),
                logEntity.getSuccess() ? "成功" : "失败",
                errorSummary
        );
    }


    // 获取实际注解（方法级别优先于类级别）
    private ApiLog getActualAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 先检查方法上是否有注解
        ApiLog methodAnnotation = method.getAnnotation(ApiLog.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        // 获取类上的注解
        return joinPoint.getTarget().getClass().getAnnotation(ApiLog.class);
    }

    /**
     * 解析 SpEL 表达式
     */
    private String parseSpEl(String expression, Object[] args, Method method) {
        if (expression == null || expression.isEmpty() || (!expression.startsWith("#") && !expression.startsWith("T("))) {
            return expression;
        }
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }
            Object result = parser.parseExpression(expression).getValue(context);
            // 如果解析结果为null，返回一个空字符串而不是表达式本身
            return result == null ? "" : result.toString();
        } catch (Exception e) {
            log.warn("SpEL表达式 '{}' 解析失败: {}", expression, e.getMessage());
            return "[SpEL_ERROR]";
        }
    }
}