/** 
* All Rights Reserved , Copyright (C) 2023 , 青岛鼎信通讯股份有限公司
* RetryLogService
* SIM卡接口传输日志
* 修改纪录
* 2023-04-22 版本：1.0 sucongcong 创建。
* @version 版本：1.0
* @author 作者：sucongcong
* 创建日期：2023-04-22
*/
package com.illsky.retry.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.illsky.retry.config.RetryConfig;
import com.illsky.retry.mapper.RetryLogMapper;
import com.illsky.retry.pojo.RetryLogEntity;
import com.illsky.retry.service.IRetryLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedList;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service(value = "retryLogService")
@Scope(value = "singleton")
@Slf4j
public class RetryLogService extends ServiceImpl<RetryLogMapper, RetryLogEntity> implements IRetryLogService {

    @Resource
    private RetryConfig retryConfig;

    /**
     * spring context
     */
    private ApplicationContext applicationContext;

    @Autowired
    public RetryLogService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }



    /**
     * @author: sucongcong
     * @date: 2023/4/25
     * @param classname 全类名
     * @param methodname 方法名
     * @param responsemsg 响应信息
     * @param retryexpirydate 重试过期时间
     * @param digest 摘要哈希值
     * @param methodParamValues 方法参数值
     * @return com.topscomm.sim.pojo.RetryLogEntity
     * @description: 新增接口日志
     * @modify:
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RetryLogEntity insertApiLog(String classname, String methodname, String responsemsg, Date retryexpirydate, String digest, Object[] methodParamValues) {
        RetryLogEntity apiLogEntity = new RetryLogEntity();
        String id = DateUtil.format(new Date(), "yyyyMMddHHmmss")+ RandomUtil.randomNumbers(4);
        apiLogEntity.setId(Long.parseLong(id));
        // 全类名
        apiLogEntity.setClassname(classname);
        // 实例名
        apiLogEntity.setNewinstancename("");
        // 方法名
        apiLogEntity.setMethodname(methodname);

        // 返回值
        apiLogEntity.setResponsemsg(responsemsg);
        // 重试有效期
        apiLogEntity.setRetryexpirydate(retryexpirydate);
        // 重试次数
        apiLogEntity.setRetrycount(0);

        // 最大重试次数
        apiLogEntity.setMaxretrycount(retryConfig.getMaxRetryCount());
        // 当天时间
        apiLogEntity.setCurrentdate(new Date());
        // 处理状态 status 0：未解决；1：已解决
        apiLogEntity.setStatus(0);
        apiLogEntity.setDescription(JSONObject.toJSONString(methodParamValues, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty));
        apiLogEntity.setDigest(digest);
        if (methodParamValues != null && methodParamValues.length > 0) {
            try {
                //反射获取类的Class，并获取所有的声明方法，遍历之，获取需要进行重试的方法(该方法不可重载，否则无法获取准确的方法)
                Class<?> clazz = Class.forName(classname);
                Method[] methods = clazz.getDeclaredMethods();
                Method calledMethod=null;
                for(Method method:methods){
                    if(method.getName().equals(methodname)){
                        calledMethod=method;
                        break;
                    }
                }
                //获取方法的参数类型，遍历参数值数组
                Class<?>[] paramTypes = calledMethod.getParameterTypes();
                List<String> paramValueStrList = new LinkedList<>();
                for (int i = 0; i < methodParamValues.length; i++) {
                    Object paramObj = methodParamValues[i];
                    //如果值为空，则存入 "null"
                    if (null == paramObj){
                        paramValueStrList.add("null");
                    }else {
                        //如果参数是String类型，则直接存入
                        if (paramTypes[i] == String.class){
                            paramValueStrList.add((String) paramObj);
                        }else {
                            //如果参数是POJO或集合类型，则序列化为字符串
                            paramValueStrList.add(JSONObject.toJSONString(paramObj));
                        }
                    }
                }
                // 方法入参 paramvalues
                apiLogEntity.setParamvalues(JSON.toJSONString(paramValueStrList));
            } catch (ClassNotFoundException ignored) {}
        }
        // 创建时间
        apiLogEntity.setCreateon(new Date());
        this.insert(apiLogEntity);
        return apiLogEntity;
    }

    /**
     * @author: sucongcong
     * @date: 2023/4/22
     * @param apiLogEntityList
     * @description: 执行补偿机制
     * @modify:
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerApiLog(List<RetryLogEntity> apiLogEntityList) {
        for (RetryLogEntity apiLogEntity : apiLogEntityList) {
            handlerApiLog(apiLogEntity);
        }
    }

    /**
     * @author: sucongcong
     * @date: 2023/4/22
     * @param apiLogEntity
     * @description: 执行补偿机制
     * @modify:
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerApiLog(RetryLogEntity apiLogEntity) {
        //反射获取类的Class，并定位到要重试的方法
        try {
            Class<?> clazz = Class.forName(apiLogEntity.getClassname());
            // 获取service实例
            Object object = applicationContext.getBean(clazz);
            Method[] methods = clazz.getDeclaredMethods();
            Method calledMethod=null;
            for(Method method:methods){
                if(method.getName().equals(apiLogEntity.getMethodname())){
                    calledMethod=method;
                    break;
                }
            }
            Object[] paramValueList = null;
            //如果方法有参数，则进行参数解析
            if (StringUtils.isNotEmpty(apiLogEntity.getParamvalues())){
                List<String> paramValueStrList = JSONObject.parseArray(apiLogEntity.getParamvalues(), String.class);
                //获取方法所有参数的Type
                Type[] paramTypes = calledMethod.getGenericParameterTypes();
                paramValueList = new Object[paramTypes.length];
                for (int i = 0; i < paramValueStrList.size(); i++) {
                    String paramStr = paramValueStrList.get(i);
                    //如果参数值为空，则置为null
                    if ("null".equalsIgnoreCase(paramStr)){
                        paramValueList[i] = null;
                    }else {
                        //如果参数是String类型，则直接赋值
                        if (paramTypes[i] == String.class){
                            paramValueList[i] = paramStr;
                            // 如果参数是带泛型的集合类或者不带泛型的List，则需要特殊处理
                        }else if(paramTypes[i] instanceof ParameterizedType || paramTypes[i] == List.class){
                            Type genericType = paramTypes[i];

                            //如果是不带泛型的List 直接解析数组
                            if (genericType == List.class){
                                paramValueList[i] = JSON.parseObject(paramStr, List.class);
                            }else if (((ParameterizedTypeImpl) genericType).getRawType() == List.class){
                                // 如果是带泛型的List，则获取其泛型参数类型
                                Type[] params = ((ParameterizedType) genericType).getActualTypeArguments();
                                //得到泛型类型对象
                                Class<?> genericClazz = null;
                                if (!(params[0]  instanceof Class)) {
                                    genericClazz = Object.class;
                                } else {
                                    //得到泛型类型对象
                                    genericClazz = (Class)params[0];
                                }
                                //反序列化
                                paramValueList[i] = JSON.parseArray(paramStr, genericClazz);
                            }else {
                                //如果是带泛型的其他集合类型，直接反序列化
                                paramValueList[i] = JSON.parseObject(paramStr, paramTypes[i], Feature.OrderedField);
                            }
                        }else {
                            //如果是POJO类型，则直接解析对象
                            paramValueList[i] = JSON.parseObject(paramStr, paramTypes[i], Feature.OrderedField);
                        }
                    }
                }
            }
            //设置访问权限，否则会调用失败，throw IllegalAccessException
            calledMethod.setAccessible(true);
            //反射调用方法
            boolean asyncFlag = false;
            Annotation[] annotations = calledMethod.getDeclaredAnnotations();
            if (annotations != null && annotations.length > 0){
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().getTypeName().equalsIgnoreCase("org.springframework.scheduling.annotation.Async")){
                        asyncFlag = true;
                    }
                }
            }
            // 是否异步调用
            if (asyncFlag){
                Future<Boolean> future = (Future) calledMethod.invoke(object, paramValueList);
                Boolean flag = future.get();
                if(!flag){
                    throw new RuntimeException();
                }
            }else {
                calledMethod.invoke(object, paramValueList);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 执行成功了要更新状态
                RetryLogEntity retryLogEntity = new RetryLogEntity();
                retryLogEntity.setRetrycount(apiLogEntity.getRetrycount() + 1);
                retryLogEntity.setModifiedon(new Date());
                retryLogEntity.setStatus(1);
                // 修改更新时间和重试次数
                this.update(retryLogEntity, new EntityWrapper<RetryLogEntity>().eq(RetryLogEntity.FieldId, apiLogEntity.getId()));
            }

        } catch (ClassNotFoundException | IllegalAccessException | InterruptedException | ExecutionException e ) {
            log.error("反射异常-->",e);
        }catch (InvocationTargetException e){
            log.error("重试调用失败,更新次数-->",e);
        }
    }

}
