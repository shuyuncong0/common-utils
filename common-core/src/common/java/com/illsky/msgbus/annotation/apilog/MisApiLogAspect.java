package com.illsky.msgbus.annotation.apilog;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author: sucongcong
 * @date: 2023/2/3
 * @descripyion: 记录对外接口日志
 * @modify:
 */
@Aspect
@Component
@Slf4j
public class MisApiLogAspect {


    // @Autowired
    // private IMisMqMessageAcceptLogService misMqMessageAcceptLogService;

    // 切入点
    @Pointcut(value = "@annotation(com.illsky.msgbus.annotation.apilog.ApiLog)")
    private void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object[] args = null;
        Object returnValue = null;
        String status = "";
        try {
            args = point.getArgs();
            returnValue = point.proceed(args);
            status = "200";
            return returnValue;
        } catch (Throwable ex) {
            status = "500";
            returnValue = ex.getMessage();
            throw ex;
        } finally {
            writeLog(status, args, returnValue);
        }
    }

    /**
     * @author: sucongcong
     * @date: 2023/2/3
     * @param status
     * @param args
     * @param returnValue
     * @description: 记录ApiLog表日志
     * @modify:
     */
    private void writeLog(String status, Object[] args, Object returnValue) {
        /*if (!ParameterCache.getBooleanValue("MisMsgAcceptLogFlage",false)) {
            return ;
        }*/
        //记录发送日志
        Object logEntity=new Object();
        if (args == null){
            // 设置日志为空，或者不新增直接返回
        } else {
            String jsonString = "";
            // jsonString = JSON.toJSONString(args);
            if (jsonString.length() > 1200) {
                jsonString = jsonString.substring(0, 1200);
            }
            // data
            // logEntity.setAcceptlog(jsonString);
        }
        // 业务对象
        // logEntity.setTablename("MsgBus");
        // 操作类型
        // logEntity.setDescription("接收信息化消息代理日志");
        log.debug("ApiLog日志记录:{}", logEntity);
        // misMqMessageAcceptLogService.insert(logEntity);

    }
}
