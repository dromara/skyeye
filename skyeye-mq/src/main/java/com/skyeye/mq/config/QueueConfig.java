/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.mq.config;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @ClassName: QueueConfig
 * @Description: 创建一个队列
 * @author 卫志强
 * @date 2019年3月3日
 *
 */
@Configuration
public class QueueConfig {
	
	@Value("${queue}")
	// 值就是skyeye
	private String queueName;

	@Bean
	public Queue queue() {// 消息队列的名字就是skyeye
		return new ActiveMQQueue(queueName);
	}
}
