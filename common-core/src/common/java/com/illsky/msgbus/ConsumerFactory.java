
package com.illsky.msgbus;
import cn.hutool.core.util.StrUtil;
import com.illsky.msgbus.annotation.msgbus.MsgBus;
import com.illsky.msgbus.service.IMsgBusService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: sucongcong
 * @date: 2023/2/3
 * @descripyion: 数据推送处理实现类工厂
 * @modify:
 */
@Service(value = "consumerFactory")
public class ConsumerFactory {

	public static Map<String, IMsgBusService> syncFacory = new HashMap<>();

	@Resource
	private ApplicationContext applicationContext;

	@PostConstruct
	private void init(){
		getRegisterClass(MsgBus.class);
	}

	/**
	 * @author: mwf
	 * @date: 2023年2月15日17:15:19
	 * @param annotationTypes 注解类型
	 * @description: 通过注解注入工厂
	 * @modify:
	 */
	@SafeVarargs
    private final void getRegisterClass(Class<? extends Annotation>... annotationTypes) {
		for (Class<? extends Annotation> annotationType : annotationTypes) {
			Map<String, Object> annotatedBeansMap = applicationContext.getBeansWithAnnotation(annotationType);
			for(Map.Entry<String, Object> entry : annotatedBeansMap.entrySet()){
				if(entry.getValue() instanceof IMsgBusService){
					Class<IMsgBusService> reflectTestClass = (Class<IMsgBusService>) entry.getValue().getClass();
					MsgBus msgBus = reflectTestClass.getAnnotation(MsgBus.class);
					syncFacory.put(msgBus.sendSys().toUpperCase()+":"+msgBus.clsName().toUpperCase(), (IMsgBusService) entry.getValue());
				}
			}
		}
	}

	/**
	 * @author: sucongcong
	 * @date: 2023/2/3
	 * @param sendSys 发送系统
	 * @param clsName 业务对象
	 * @param msgBusService 实现类
	 * @description: 注入实现类
	 * @modify:
	 */
	public static void register(String sendSys,String clsName ,IMsgBusService msgBusService){
		if(!StrUtil.isEmpty(sendSys)&&!StrUtil.isEmpty(clsName)){
			syncFacory.put(sendSys.toUpperCase()+":"+clsName.toUpperCase(),msgBusService);
		}
	}

	/**
	 * @author: sucongcong
	 * @date: 2023/2/3
	 * @param sendSys 发送系统
	 * @param clsName 业务对象
	 * @description: 根据业务对象获取不同的实现类
	 * @modify:
	 */
	public static IMsgBusService getMsgBusService(String sendSys,String clsName ){
		return syncFacory.get(sendSys.toUpperCase()+":"+clsName.toUpperCase());
	}

}
