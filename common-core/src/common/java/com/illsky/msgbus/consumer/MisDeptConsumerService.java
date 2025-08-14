package com.illsky.msgbus.consumer;

import cn.hutool.json.JSONObject;
import com.illsky.msgbus.ConsumerFactory;
import com.illsky.msgbus.annotation.empotent.MsgEmpotent;
import com.illsky.msgbus.pojo.ResponseResult;
import com.illsky.msgbus.pojo.msgbus.MsgBusDTO;
import com.illsky.msgbus.service.IMsgBusService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * @author: sucongcong
 * @date: 2023/3/10
 * @descripyion: 运营部门消息代理消费者
 * @modify:
 */
@Service(value = "misDeptConsumerService")
@Scope(value = "singleton")
public class MisDeptConsumerService implements IMsgBusService{


    @Resource
    private ApplicationContext applicationContext;

    /**
     * 将当前实现类注册到工厂中
     *
     * @author sucongcong
     * @since 2024-04-17 14:37
     */
    @PostConstruct
    private void init(){
        ConsumerFactory.register("EHR","MisDept", (IMsgBusService) applicationContext.getBean("misDeptConsumerService"));
    }
    /**
     * 运营部门信息插入
     *
     * @param msgBusDTO
     * @return
     * @author sucongcong
     * @since 2024-04-23 10:06
     */
    @MsgEmpotent
    public ResponseResult<Object> insert(MsgBusDTO msgBusDTO) {
        List<JSONObject> list = msgBusDTO.getData();
        List<Object> misDeptInsertList = new ArrayList<>();

        // id拼接为字符串，用于数据库查询
       /* String idsStr = list.stream()
                .map(item -> "'" + item.get(MisDeptEntity.FieldId) + "'")
                .collect(Collectors.joining(","));
        List<Map<String, Object>> idsList = misDeptService.queryFieldsByIds(idsStr, Collections.singletonList(MisDeptEntity.FieldId));
        // 数据库中部门id拼接为set
        Set<String> idsSet = idsList.stream()
                .map(item -> ConvertUtil.convertToString(item.get(MisDeptEntity.FieldId)))
                .collect(Collectors.toSet());

        // 将部门id不在数据库中的数据插入
        misDeptInsertList = list.stream()
                .filter(entity -> !idsSet.contains(ConvertUtil.convertToString(entity.get(MisDeptEntity.FieldId))))
                .map(item -> JSONUtil.toBean(item, MisDeptEntity.class))
                .collect(Collectors.toList());
        // 批量插入
        if (!misDeptInsertList.isEmpty()) {
            misDeptService.insertBatch(misDeptInsertList);
        }*/
        return ResponseResult.ok("插入成功");
    }

    /**
     * 运营部门信息修改
     *
     * @param msgBusDTO
     * @return
     * @author sucongcong
     * @since 2024-04-17 14:55
     */
    @MsgEmpotent
    public ResponseResult<Object> update(MsgBusDTO msgBusDTO) {
        List<JSONObject> list = msgBusDTO.getData();
        // 消息总线消息体
        List<Object> misDeptUpdateList = null;

        // id拼接为字符串，用于数据库查询
      /*  String idsStr = list.stream()
                .map(item -> "'" + item.get(MisDeptEntity.FieldId) + "'")
                .collect(Collectors.joining(","));
        List<Map<String, Object>> idsList = misDeptService.queryFieldsByIds(idsStr, Collections.singletonList(MisDeptEntity.FieldId));
        // 数据库中部门id拼接为set
        Set<String> idsSet = idsList.stream()
                .map(item -> ConvertUtil.convertToString(item.get(MisDeptEntity.FieldId)))
                .collect(Collectors.toSet());

        // 将部门id在数据库中的数据更新
        misDeptUpdateList = list.stream()
                .filter(entity -> idsSet.contains(ConvertUtil.convertToString(entity.get(MisDeptEntity.FieldId))))
                .map(item -> JSONUtil.toBean(item, MisDeptEntity.class))
                .collect(Collectors.toList());
        misDeptService.updateBatch(misDeptUpdateList);*/
        return ResponseResult.ok("修改成功");
    }
}
