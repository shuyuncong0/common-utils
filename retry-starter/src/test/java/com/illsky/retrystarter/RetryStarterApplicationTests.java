package com.illsky.retrystarter;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.illsky.retry.pojo.RetryLogEntity;
import com.illsky.retry.service.IRetryLogService;
import com.illsky.service.IRetryTestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { RetryStarterApplication.class })
public class RetryStarterApplicationTests {

	@Resource
	private RetryService servide;

	@Resource
	public IRetryLogService retryLogService;

	@Resource(name = "retryTest2Service")
	private IRetryTestService retryTest2Service;

	@Resource(name = "retryTest1Service")
	private IRetryTestService retryTest1Service;
	@Test
	public void testRetr() {
		try {
			retryTest2Service.testString("test2", "test2");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			retryTest1Service.testString("test1", "test1");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRetry() {
		try {
			servide.testString("test");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Map map = new HashMap<>();
			map.put("key1", "value1");
			map.put("key2", "value2");
			servide.testMap(map);
		} catch (Exception e) {
			e.printStackTrace();
		}

		RetryLogEntity retryLogEntity = new RetryLogEntity();
		retryLogEntity.setUrl("url");
		retryLogEntity.setMethodname("methodname");
		retryLogEntity.setClassname("classname");
		retryLogEntity.setCategory("category");
		try {
			servide.testObj(retryLogEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			List<RetryLogEntity> list = new ArrayList<>();
			list.add(retryLogEntity);
			servide.testList(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void exeRetry() {
		List<RetryLogEntity> list = retryLogService.selectList(new EntityWrapper<RetryLogEntity>()
				.eq(RetryLogEntity.FieldStatus, "0")
						.and("retrycount <= maxretrycount").and("retryexpirydate > sysdate"));
//		selectList(new EntityWrapper<RetryLogEntity>().eq(RetryLogEntity.FieldStatus, status).eq(RetryLogEntity.FieldDigest, digest));
//		List<TsApiLogEntity> tsApiLogEntities = tsApiLogService.queryByWhere(" status = '0' and retrycount <= maxretrycount and retryexpirydate < sysdate  ");
		for (RetryLogEntity retryLogEntity : list) {
			retryLogService.handlerApiLog(retryLogEntity);
		}

	}


}
