/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.jedis.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 读取redis配置信息并装载
 * 
 * @author 卫志强
 *
 */
@Component
@Data
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
	
	private int expireSeconds;
	
	private String cluster;
	
	private int commandTimeout;

	private String password;

	// 单机版参数
	@Value("${redis.single.host}")
	private String singleHost;

	@Value("${redis.single.port}")
	private int singlePort;

}
