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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
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
        // 获取实际的注解配置（优先使用方法上的注解）
        ApiLog apiLog = getActualAnnotation(joinPoint);
        // 没有注解直接执行原方法
        if (apiLog == null) {
            return joinPoint.proceed();
        }
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        // 构建日志对象
        TApiLogPO logEntity = TApiLogPO.builder()
                .createTime(new Date())
                .build();

        // 设置基本信息
        recordBasicInfo(logEntity, apiLog, joinPoint);
        // 设置请求信息
        recordRequestInfo(logEntity, joinPoint);


        Object result = null;
        boolean success = true;
        String errorMessage = null;
        try {
            // 执行原方法
            result = joinPoint.proceed();
            // 设置响应信息
            if (result != null) {
                try {
                    logEntity.setResponseBody(objectMapper.writeValueAsString(result));
                } catch (Exception e) {
                    logEntity.setResponseBody(result.toString());
                }
            }
            return result;
        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
            // 设置异常信息
            logEntity.setErrorMessage(getExceptionInfo(e));
            // 抛出原始异常
            throw e;
        } finally {
            // 设置执行时间和状态
            long executionTime = System.currentTimeMillis() - startTime;
            logEntity.setExecutionTime(executionTime);
            logEntity.setSuccess(success);
            // 异步保存日志到数据库
            saveLogAsync(logEntity);
            // 根据日志级别控制台输出
            logByLevel(apiLog.level(), buildLogMessage(logEntity, executionTime, success, errorMessage));
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

        // 请求ID
        if (!apiLog.requestId().isEmpty()) {
            logEntity.setRequestId(parseSpEl(apiLog.requestId(), joinPoint.getArgs(), method));
        }

        // 尝试获取业务类型（从方法名中提取）
        String methodName = method.getName();
        if (methodName.startsWith("get") || methodName.startsWith("query")) {
            logEntity.setBusinessType("QUERY");
        } else if (methodName.startsWith("save") || methodName.startsWith("create")) {
            logEntity.setBusinessType("CREATE");
        } else if (methodName.startsWith("update")) {
            logEntity.setBusinessType("UPDATE");
        } else if (methodName.startsWith("delete")) {
            logEntity.setBusinessType("DELETE");
        } else {
            logEntity.setBusinessType("OTHER");
        }
    }

    /**
     * 设置请求信息
     */
    private void recordRequestInfo(TApiLogPO logEntity, ProceedingJoinPoint joinPoint) {
        try {
            // 获取HTTP请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            // 设置HTTP相关信息
            logEntity.setHttpMethod(request.getMethod());
            logEntity.setRequestUri(request.getRequestURI());
            logEntity.setClientIp(getClientIp(request));
            // 设置追踪ID（如果有）
            String traceId = request.getHeader("X-Trace-Id");
            if (traceId != null && !traceId.isEmpty()) {
                logEntity.setTraceId(traceId);
            } else {
                logEntity.setTraceId(UUID.randomUUID().toString().replace("-", ""));
            }

            // 设置请求参数
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                logEntity.setRequestParams(objectMapper.writeValueAsString(args));
                // 区分请求参数和请求体
                if (isRequestBodyRequest(request)) {
                    // 如果是JSON请求，第一个参数通常是请求体
                    logEntity.setRequestBody(objectMapper.writeValueAsString(args[0]));
                }
            }

        } catch (Exception e) {
            // 非Web环境，忽略HTTP相关信息
            log.debug("非Web环境，无法获取HTTP请求信息: {}", e.getMessage());
        }
    }

    /**
     * 判断是否为请求体请求（JSON请求）
     */
    private boolean isRequestBodyRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains("application/json");
    }

    /**
     * 获取客户端IP地址
     */
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
    private String getExceptionInfo(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getName()).append(": ").append(e.getMessage());

        // 只记录前5行堆栈，避免日志过长
        StackTraceElement[] stackTrace = e.getStackTrace();
        int maxLines = Math.min(5, stackTrace.length);

        for (int i = 0; i < maxLines; i++) {
            sb.append("\n\tat ").append(stackTrace[i].toString());
        }

        if (stackTrace.length > maxLines) {
            sb.append("\n\t... ").append(stackTrace.length - maxLines).append(" more");
        }

        return sb.toString();
    }

    /**
     * 异步保存日志
     */
    @Async
    public void saveLogAsync(TApiLogPO logEntity) {
        try {
            CompletableFuture.runAsync(() -> {
                try {
                    // createApiLog(logEntity);
                } catch (Exception e) {
                    log.info("保存API日志失败: {}", e.getMessage());
                }
            }, apiLogTaskThreadPool);
        } catch (Exception e) {
            log.error("保存API日志失败: {}", e.getMessage());
        }
    }

    /**
     * 根据日志级别输出日志
     */
    private void logByLevel(ApiLog.LogLevel level, String message) {
        switch (level) {
            case DEBUG:
                log.debug(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case INFO:
            default:
                log.info(message);
                break;
        }
    }

    /**
     * 构建日志消息
     */
    private String buildLogMessage(TApiLogPO logEntity, long executionTime, boolean success, String errorMessage) {
        return String.format("API调用日志 - 来源系统: %s, 业务类型: %s, 操作: %s, 执行时间: %dms, 结果: %s, 错误信息: %s",
                logEntity.getSourceSystem(),
                logEntity.getBusinessType(),
                logEntity.getOperation(),
                logEntity.getExecutionTime(),
                logEntity.getSuccess() ? "成功" : "失败",
                errorMessage);
    }


    // 获取实际注解（方法级别优先于类级别）
    private ApiLog getActualAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 先检查方法上是否有注解
        ApiLog methodAnnotation  = method.getAnnotation(ApiLog.class);
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
        if (expression == null || expression.isEmpty()) {
            return "";
        }
        // 如果是 SpEL 表达式（以 # 或 T( 开头），则走 SpringEL 解析
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (!expression.startsWith("#") && !expression.startsWith("T(")) {
            // 如果不是 SpEL 表达式，则直接返回
            return expression;
        }
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
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

}
