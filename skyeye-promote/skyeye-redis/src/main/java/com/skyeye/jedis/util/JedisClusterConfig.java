package com.skyeye.jedis.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

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
