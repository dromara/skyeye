/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */

package com.skyeye.jedis.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 *
 * @ClassName: JedisClusterConfig
 * @Description: jedis服务启动配置类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/26 9:53
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Configuration
public class JedisClusterConfig {

	@Autowired
	private RedisProperties redisProperties;

	@Bean
	public JedisCluster getJedisCluster() {
		String[] serverArray = {redisProperties.getIp1() + ":" + redisProperties.getHost1(), redisProperties.getIp2() + ":" + redisProperties.getHost2()
								, redisProperties.getIp3() + ":" + redisProperties.getHost3(), redisProperties.getIp4() + ":" + redisProperties.getHost4()
								, redisProperties.getIp5() + ":" + redisProperties.getHost5(), redisProperties.getIp6() + ":" + redisProperties.getHost6()};
		Set<HostAndPort> nodes = new HashSet<>();

		for (String ipPort : serverArray) {
			String[] ipPortPair = ipPort.split(":");
			nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
		}
		return new JedisCluster(nodes, redisProperties.getCommandTimeout());
	}

}
