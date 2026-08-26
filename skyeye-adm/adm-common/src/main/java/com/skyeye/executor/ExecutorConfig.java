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
 * @Description: 异步任务配置类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/18 22:11
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Configuration
@ManagedResource
public class ExecutorConfig {

    @Bean(name = "messageStreamExecutor")
    public Executor getDetailsAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(1000000);
        executor.setThreadNamePrefix("messageStreamExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "knowledgeSyncExecutor")
    public Executor knowledgeSyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("knowledgeSyncExecutor-");
        executor.initialize();
        return executor;
    }
}
