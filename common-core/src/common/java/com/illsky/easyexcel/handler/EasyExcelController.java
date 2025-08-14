package com.illsky.easyexcel.handler;


import cn.hutool.core.util.StrUtil;
import com.illsky.msgbus.annotation.apilog.ApiLog;
import com.illsky.msgbus.pojo.ResponseResult;
import com.illsky.msgbus.pojo.msgbus.MsgBusDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("mis/excel")
@Slf4j
public class EasyExcelController {

    @Autowired
    @Qualifier("simPageHandlerThreadPool")
    private ExecutorService pageHandler;
    /**
     * @author: sucongcong
     * @date: 2023/2/3
     * @param msgBusDTO
     * @return com.topscomm.pub.vo.ResponseResult
     * @description: 消息处理
     * @modify:
     */
    @PostMapping("/handler")
    @ApiLog
    public ResponseResult<Object> hander(@RequestBody @Validated MsgBusDTO msgBusDTO) {

        return ResponseResult.error("消息处理异常！");
    }

    /**
     * 多线程分页查询
     *
     * @param paraMap
     * @param num
     * @return
     * @author sucongcong
     * @since 2024-05-10 16:40
     */
    public List<Map<String, Object>> queryTableData(Map<String, Object> paraMap, int num) {
        if (!paraMap.containsKey("selectSql")) {
            return null;
        }
        int totalCount = 100;
        // totalCount = this.getCount(paraMap,"getTableCount");
        List<Map<String, Object>> simFieldsList = Collections.synchronizedList(new ArrayList<>());
        //每次查询的条数
        num = num == 0 ? 5000 : num;
        int number = num;
        //需要查询的次数
        int times = totalCount / num;
        if (totalCount % num != 0) {
            times += 1;
        }
        // 定义计数器，用于判断线程是否执行完毕
        CountDownLatch countDownLatch = new CountDownLatch(times);
        // 计算sql语句中每个分页查询的起始和结束数据下标
        for (int i = 0; i < times; i++) {
            int index = i + 1;
            CompletableFuture.supplyAsync(() -> this.queryTableDataPage(StrUtil.toString(paraMap.get("selectSql")),
                            StrUtil.toString(paraMap.get("whereSql")), index, number), pageHandler)
                    .thenApplyAsync(list -> {
                        simFieldsList.addAll(list);
                        return list;
                    }, pageHandler)
                    .whenCompleteAsync((list, e) -> {
                        // 子线程数据处理完毕后计数器减一
                        countDownLatch.countDown();
                        if (e != null) {
                            log.error("异常信息：{}", e.getMessage());
                        }
                    }, pageHandler);
        }
        try {
            // 主线程阻塞等待子线程执行
            boolean await = countDownLatch.await(1, TimeUnit.HOURS);
            log.info("线程等待结果：{}", await);
        } catch (InterruptedException e) {
            log.error("线程等待异常", e);
        }
        return simFieldsList;
    }

    private List<Map<String, Object>> queryTableDataPage(String selectSql, String whereSql, int curPageIndex, int pagesize) {
        List<Map<String, Object>> tempList = new ArrayList<>();
        if (pagesize > 0) {
            // tempList = this.queryTableMapForPage(selectSql, whereSql, curPageIndex, pagesize, "queryTableInfoForPage");
        }
        return tempList;
    }


}
