/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com;

import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@EnableAutoConfiguration(exclude={
	org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
	org.flowable.spring.boot.FlowableSecurityAutoConfiguration.class
})
@EnableConfigurationProperties({FlowableModelerAppProperties.class})
@EnableTransactionManagement//启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@EnableScheduling
public class SkyEyeApplication {

    @Value("${IMAGES_PATH}")
    private String tPath;
	
	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(SkyEyeApplication.class, args);
	}
	
	/**  
     * 文件上传配置  
     * @return  
     */  
    @Bean  
    public MultipartConfigElement multipartConfigElement() {  
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(tPath);
        //单个文件最大  
        factory.setMaxFileSize("102400KB");//KB,MB 100MB  
        //设置总上传数据总大小  
        factory.setMaxRequestSize("1024000KB");// 1000MB
        return factory.createMultipartConfig();  
    }

    /**
     * 解决同一时间启动的多个个定时任务(都设置了5秒运行一次)只会运行一个的问题
     *
     * @return
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(50);
        return taskScheduler;
    }
	
}
