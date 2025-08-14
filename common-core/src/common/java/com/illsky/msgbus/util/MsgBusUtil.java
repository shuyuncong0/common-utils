/**
 * @author: jichanghong
 * @date: 2018年8月24日下午5:36:28
 * @description:
 * @modify:
 */
package com.illsky.msgbus.util;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: sucongcong
 * @date: 2022/10/13
 * @descripyion: 工具类
 * @modify:
 */
@Slf4j
public class MsgBusUtil {

	/**
	 * @author: sucongcong
	 * @date: 2022/10/13
	 * @param listEntity
	 * @return java.util.List
	 * @description: 对象数组转对象map
	 * @modify:
	 */
	public static List listObjectToMap(List<Object> listEntity){
		List<Map> listMap = new ArrayList<>();
		for (Object basicEntity : listEntity) {
			// listMap.add(basicEntity.convertToMap());
		}
		return listMap;
	}

	/**
	 * @author: 苏聪聪
	 * @date: 2022年12月8日下午3:24:48
	 * @param system 系统编号
	 * @param message  消息体
	 * @return_type:成功返回"",异常返回错误信息。
	 * @description:下发密码修改事件
	 * @modify:
	 */
	public static String sendMsgBusEvent(String system,String message) {

		/*try {
			// 适配 平台2.0 系统
			String url = Config.getInstance().getValue(MisSystemConst.MsgBusConfig.BASEMBS_FULL_URL, "");
			if (StringUtils.isBlank(url)) {
				//消息代理系统地址
				url = ParameterCache.getValue(MisSystemConst.MsgBusConfig.BASEMBS_URL) + MisSystemConst.MsgBus.PUSH_MSG_BUS_EVENT;
			}
			//组织事件的headers
			Map<String, String> headers = new HashMap<>(8);
			headers.put(MisSystemConst.MsgBusConfig.CONTENT_TYPE, "application/json");
			headers.put(MisSystemConst.MsgBusConfig.CLIENT_SYSTEM, system);
			headers.put(MisSystemConst.MsgBusConfig.CLIENT_SECURE, ParameterCache.getValue(MisSystemConst.MsgBusConfig.BASEMBS_TOKEN));
			// 接口调用
			log.debug("发送消息代理,报文：{}",message);
			String result = HttpUtil.createPost(url).timeout(30000).addHeaders(headers).body(message).execute().body();
			log.debug("发送消息代理,结果：{}",result);
			return result;
		}catch (Exception e){
			log.error("发送消息代理，异常",e);
			throw new BusinessException(e.getMessage());
		}*/
		return "";
	}
}
