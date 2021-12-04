/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.jedis.util;

import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.impl.JedisClientServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class JedisClusterConfig {

	@Autowired
	private RedisProperties redisProperties;
	
	private static final Logger logger = LoggerFactory.getLogger(JedisClusterConfig.class);

	// 是否开启集群 true：是 false：否
	@Value("${spring.redis.isJq}")
	private boolean isJq;

	/**
	 * JedisPoolConfig 连接池
	 * @return
	 */
	@Bean(name = "jedisPoolConfig")
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 最大空闲连接数
		jedisPoolConfig.setMaxIdle(500);
		// 最小空闲连接数
		jedisPoolConfig.setMinIdle(20);
		// 最大连接数
		jedisPoolConfig.setMaxTotal(800);
		jedisPoolConfig.setBlockWhenExhausted(true);
		jedisPoolConfig.setTestOnBorrow(true);
		// Idle时进行连接扫描
		jedisPoolConfig.setTestWhileIdle(true);
		// 表示idle object evitor每次扫描的最多的对象数
		jedisPoolConfig.setNumTestsPerEvictionRun(10);
		// 表示idle object evitor两次扫描之间要sleep的毫秒数
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
		// 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
		// evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
		jedisPoolConfig.setMinEvictableIdleTimeMillis(60000);
		jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(60000);
		// 最大阻塞等待时间，负值为无限制
		jedisPoolConfig.setMaxWaitMillis(10000);
		return jedisPoolConfig;
	}

    @Bean(name = "redisClusterConfiguration")
    public RedisClusterConfiguration getRedisClusterConfiguration() {
        RedisClusterConfiguration configuration = new RedisClusterConfiguration();
        Set<RedisNode> nodes = new HashSet<>();

        String[] serverArray = redisProperties.getCluster().split(",");
        for (String ipPort : serverArray) {
            String hostName = ipPort.split(":")[0];
            int port = Integer.parseInt(ipPort.split(":")[1]);
            RedisNode node = new RedisNode(hostName, port);
            nodes.add(node);
        }

        configuration.setClusterNodes(nodes);
        return configuration;
    }

	/**
	 * 单机版和集群版配置
	 * @Title: JedisConnectionFactory
	 * @param @param jedisPoolConfig
	 * @param @return
	 * @return JedisConnectionFactory
	 * @autor lpl
	 * @date 2018年2月24日
	 * @throws
	 */
	@Bean(name = "jedisConnectionFactory")
	public JedisConnectionFactory jedisConnectionFactory(
			@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig,
			@Qualifier("redisClusterConfiguration") RedisClusterConfiguration redisClusterConfiguration) {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		// 是否开启集群模式
		if (isJq) {
			// 集群模式
			jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration, jedisPoolConfig);
		} else {
			// 单机模式
			jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
			// IP地址
			jedisConnectionFactory.setHostName(redisProperties.getSingleHost());
			// 端口号
			jedisConnectionFactory.setPort(redisProperties.getSinglePort());
		}
		//	判断密码是否存在，存在设置值
		checkPasswordIfNull(jedisConnectionFactory);
		return jedisConnectionFactory;
	}

	/**
	 * 校验password是否为空，不为空设置密码
	 * @param jedisConnectionFactory
	 */
	private void checkPasswordIfNull(JedisConnectionFactory jedisConnectionFactory) {
		if (!ToolUtil.isBlank(redisProperties.getPassword())) {
			jedisConnectionFactory.setPassword(redisProperties.getPassword());
		}
	}

	/**
	 * 实例化 RedisTemplate 对象
	 *
	 * @return
	 */
	@Bean(name = "redisTemplate")
	public RedisTemplate getRedisTemplate(
			@Qualifier("jedisConnectionFactory") JedisConnectionFactory factory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(factory);
		// 设置序列化Key的实例化对象
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		// 设置序列化Value的实例化对象
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return redisTemplate;
	}

	@Bean(name = "jedisClientServiceImpl")
	public JedisClientServiceImpl getJedisCluster(RedisTemplate redisTemplate){
		JedisClientServiceImpl jedisClientServiceImpl = new JedisClientServiceImpl();
		jedisClientServiceImpl.setRedisTemplate(redisTemplate);
		return jedisClientServiceImpl;
	}


	/**
	 * 自定义缓存时，keyGenerator 的生成规则
	 */
	@Bean
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... objects) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getName());
				sb.append(method.getName());
				for (Object obj : objects) {
					sb.append(obj.toString());
				}
				return sb.toString();
			}
		};
	}

	/**
	 * 管理缓存
	 */
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory) {
		// 更改值的序列化方式，否则在Redis可视化软件中会显示乱码。默认为JdkSerializationRedisSerializer
		RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer());
		RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.serializeValuesWith(pair) // 设置序列化方式
				.entryTtl(Duration.ofHours(1)); // 设置过期时间

		return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(factory))
				.cacheDefaults(defaultCacheConfig).build();
	}

}
