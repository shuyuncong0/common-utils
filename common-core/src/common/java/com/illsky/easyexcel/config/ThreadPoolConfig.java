package com.illsky.easyexcel.config;


import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author: sucongcong
 * @date: 2023/4/17
 * @descripyion: 自定义线程池
 * @modify:
 */
@Configuration
@Slf4j
public class ThreadPoolConfig {

    /** 获取当前系统的CPU 数目*/
    private static final int cpuNums = Runtime.getRuntime().availableProcessors();
    /** 线程池核心池的大小*/
    private static final int corePoolSize = cpuNums * 2;
    /** 线程池的最大线程数*/
    private static final int maximumPoolSize = cpuNums * 3;


    /**
     * @author: sucongcong
     * @date: 2023/4/17
     * @return java.util.concurrent.ExecutorService
     * @description: 自定义线程池
     * @modify:
     */
    @Bean(value = "simHandlerThreadPool")
    public ExecutorService buildSimHandlerThreadPool() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNamePrefix("任务处理线程-").build();
        log.info("simHandlerThreadPool 创建核心线程数:{},最大线程数:{}" , corePoolSize , maximumPoolSize);
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 5,TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000000), namedThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        executorService.allowCoreThreadTimeOut(true);
        return executorService;
    }
}
