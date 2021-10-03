/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @ClassName: SkyEyeApplication
 * @Description: 启动类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/3 21:14
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude={
	org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class, 
	org.activiti.spring.boot.SecurityAutoConfiguration.class
})
@EnableTransactionManagement
@EnableScheduling
public class SkyEyeApplication {
	
	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(SkyEyeApplication.class, args);
	}

}
