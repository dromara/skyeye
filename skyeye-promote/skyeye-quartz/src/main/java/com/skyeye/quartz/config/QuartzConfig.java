/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */

package com.skyeye.quartz.config;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @ClassName: QuartzConfig
 * @Description: 定时任务配置类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/10 20:35
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Configuration
public class QuartzConfig {

    @Autowired
    private JobFactory jobFactory;

    @Bean(name="SchedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean()  {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setAutoStartup(true);
        return factory;
    }

   /**
    * 
        * @Title: scheduler
        * @Description: 通过SchedulerFactoryBean获取Scheduler的实例
        * @param @return    参数
        * @return Scheduler    返回类型
        * @throws
    */
    @Bean(name="scheduler")
    public Scheduler scheduler()  {
        return schedulerFactoryBean().getScheduler();
    }

}
