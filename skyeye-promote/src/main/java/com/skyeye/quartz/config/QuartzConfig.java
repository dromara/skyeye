package com.skyeye.quartz.config;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

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
