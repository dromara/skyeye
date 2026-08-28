/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @ClassName: ExecutorConfig
 * @Description: 自动化模块异步任务配置
 * @author: skyeye云系列--卫志强
 * @date: 2026/7/23
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Configuration
@ManagedResource
public class ExecutorConfig {

    /**
     * 定时任务用例执行线程池。
     * IO 密集（接口/断言），核心线程数控制并发，大队列削峰。
     */
    @Bean(name = "scheduleTaskExecutor")
    public Executor getScheduleTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("scheduleTaskExecutor-");
        executor.initialize();
        return executor;
    }

    /**
     * 定时任务批次内用例并行执行线程池
     */
    @Bean(name = "scheduleCaseExecutor")
    public Executor getScheduleCaseExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("scheduleCaseExecutor-");
        executor.initialize();
        return executor;
    }
}
