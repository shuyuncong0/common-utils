package com.illsky.redis;

import cn.hutool.core.thread.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: succ
 * @date: 2023-03-24
 * @description: TODO
 * @modiFy:
 */
@Component
public class MisRedisInfoService  {

    // 缓存过期时间(分钟)
    private static final int cacheLiveTime = 10;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // @Autowired
    // private IMisDeptService misDeptService;




    /**
     * @author: sucongcong
     * @date: 2023/3/24
     * @return com.topscomm.mis.pojo.MisDeptEntity
     * @description: 当前登录人运营部门信息
     * @modify:
     */
    public Object queryUserMisDept() {
        // redis存储当前用户的前綴
        final String REDIS_KEY_PREFIX = "user:misdept:";
        String usercode = "";
        String key = REDIS_KEY_PREFIX + usercode;
        Boolean hasKey = redisTemplate.hasKey(key);
        Object misDeptEntity = null;
        if (Boolean.TRUE.equals(hasKey)) {
            // 缓存存在
            misDeptEntity = (Object) redisTemplate.opsForValue().get(key);
        } else {
            // 获取当前登录用户信息
            String deptcode = "";
            // 获取当前登录用户运营部门信息
            // misDeptEntity = misDeptService.searchDeptByCode(deptcode, Boolean.TRUE);

            if (misDeptEntity == null ) {
                // 获取用户最近的运营部门，【如果当人事部门没有关联运营部门，则找上级运营部门，直到找到为止】
                // String misdeptcode = misDeptService.querySuperiorDeptByUserCode(usercode);
                // misDeptEntity = misDeptService.searchDeptByCode(misdeptcode);
            }
            if (misDeptEntity != null) {
                redisTemplate.opsForValue().set(key, misDeptEntity, cacheLiveTime, TimeUnit.MINUTES);
            }
        }
        return misDeptEntity;
    }

    /**
     * @author: sucongcong
     * @date: 2023/3/25
     * @param tableName
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description: 从redis中获取缓存数据
     * @modify:
     */
    public List<Map<String, Object>> queryCacheList(String tableName) {
        String key = tableName.toLowerCase();
        Boolean hasKey = redisTemplate.hasKey(key);
        List<Map<String, Object>> listMap = null;
        if (Boolean.TRUE.equals(hasKey)) {
            // 缓存存在
            listMap = getListFromRedis(key);
        }else {
            // 缓存不存在
            listMap = this.queryMisDeptFieldList();
            try {
                if (listMap != null && !listMap.isEmpty()){
                    redisTemplate.opsForList().rightPushAll(key, listMap);
                    redisTemplate.expire(key, cacheLiveTime, TimeUnit.MINUTES);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return listMap;
    }

    /**
     * @author: sucongcong
     * @date: 2023/11/24
     * @param key
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description: 从redis中获取 listmap 数据
     * @modify:
     */
    public List<Map<String, Object>> getListFromRedis(String key) {
        List<Object> originalList = redisTemplate.opsForList().range(key, 0, -1);
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (originalList == null) {
            return null;
        }
        for (Object obj : originalList) {
            if (obj instanceof Map) {
                // 将每个元素转换为Map<String, Object>
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                resultList.add(map);
            }
            // 如果元素不是Map，你可能需要其他的转换逻辑或抛出异常处理
        }
        return resultList;
    }


    /**
     * @author: sucongcong
     * @date: 2023/3/24
     * @param tableName
     * @description: 延期删除redis中的缓存数据
     * @modify:
     */
    public void delayClearCache(String tableName) {
        // redis存储当前用户的前綴
        String key = tableName.toLowerCase();
        ThreadUtil.execAsync(() -> {
            // 最多等待主线程2000mills
            final int everyMills = 2000;
            try {
                // 为了等待主线程事务提交,挂起当前线程
                Thread.sleep(everyMills);
            } catch (InterruptedException ignored) {

            }
            this.ClearCache(key);
        }, false);
    }

   /**
    * @author: sucongcong
    * @date: 2023/3/25
    * @param tableName
    * @description: 删除redis中的缓存数据
    * @modify:
    */
    public void ClearCache(String tableName) {
        String key = tableName.toLowerCase();
        redisTemplate.delete(key);
    }

    /**
     * @author: sucongcong
     * @date: 2023/3/25
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description: 查询运营部门列表
     * @modify:
     */
    private List<Map<String, Object>> queryMisDeptFieldList() {
        // 缓存不存在
        /*List<String> filedList = new ArrayList<>();
        filedList.add(MisDeptEntity.FieldId);
        filedList.add(MisDeptEntity.FieldDeptcode);
        filedList.add(MisDeptEntity.FieldDeptname);
        filedList.add(MisDeptEntity.FieldLevelcode);
        filedList.add(MisDeptEntity.FieldParentcode);
        filedList.add(MisDeptEntity.FieldFullname);
        filedList.add(MisDeptEntity.FieldSystemcode);
        filedList.add(MisDeptEntity.FieldDeptlevel);
        filedList.add(MisDeptEntity.FieldDepttype);
        filedList.add(MisDeptEntity.FieldHeadercode);
         misDeptService.queryMapFieldsByWhere("enabled=1", filedList);*/
        return new ArrayList<>();
    }






}
