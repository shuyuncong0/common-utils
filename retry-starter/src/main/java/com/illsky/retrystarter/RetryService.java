package com.illsky.retrystarter;

import com.illsky.retry.annotation.RetryExceptionHandler;
import com.illsky.retry.exception.RetryException;
import com.illsky.retry.pojo.RetryLogEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: succongccong
 * @date: 2023-06-04
 * @description: TODO
 * @modiFy:
 */
@Service(value = "retryService")
public class RetryService {
    @RetryExceptionHandler()
    public void testString(String sourcetype) {
        System.out.println("deptHandler:"+sourcetype);
        Map mapList = null;
        if (mapList == null){
            throw new RetryException();
        }
    }

    @RetryExceptionHandler()
    public void testMap(Map<String, Object> map) {
        System.out.println("deptHandler:"+map);
        Map mapList = null;
        if (mapList == null){
            throw new RetryException();
        }
    }

    @RetryExceptionHandler()
    public void testObj(RetryLogEntity retryLogEntity) {
        System.out.println("deptHandler:"+retryLogEntity);
        Map mapList = null;
        if (mapList == null){
            throw new RetryException();
        }
    }

    @RetryExceptionHandler()
    public void testList(List<RetryLogEntity> list) {
        System.out.println("deptHandler:"+list);
        Map mapList = null;
        if (mapList == null){
            throw new RetryException();
        }
    }

}
