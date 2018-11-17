package com.skyeye.jedis.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.skyeye.jedis.JedisClient;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;
import redis.clients.util.Slowlog;

/**
 * 
     * @ClassName: JedisClientPool
     * @Description: redis集群
     * @author 卫志强
     * @date 2018年11月17日
     *
 */
public class JedisClientPool implements JedisClient {
	
	@Autowired
	private JedisPool jedisPool;

	@Override
	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.set(key, value);
		jedis.close();
		return result;
	}

	@Override
	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.get(key);
		jedis.close();
		return result;
	}

	@Override
	public Boolean exists(String key) {
		Jedis jedis = jedisPool.getResource();
		Boolean result = jedis.exists(key);
		jedis.close();
		return result;
	}

	@Override
	public Long expire(String key, int seconds) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.expire(key, seconds);
		jedis.close();
		return result;
	}

	@Override
	public Long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.ttl(key);
		jedis.close();
		return result;
	}

	@Override
	public Long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.incr(key);
		jedis.close();
		return result;
	}

	@Override
	public Long hset(String key, String field, String value) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.hset(key, field, value);
		jedis.close();
		return result;
	}

	@Override
	public String hget(String key, String field) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.hget(key, field);
		jedis.close();
		return result;
	}

	@Override
	public Long hdel(String key, String... field) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.hdel(key, field);
		jedis.close();
		return result;
	}

	@Override
	public Long del(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.del(SafeEncoder.encode(key));
		jedis.close();
		return result;
	}
	
	@Override
	public String getRedisInfo() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Client client = jedis.getClient();
			client.info();
			String info = client.getBulkReply();
			return info;
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	@Override
	public List<Slowlog> getLogs(long entries) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			List<Slowlog> logList = jedis.slowlogGet(entries);
			return logList;
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	@Override
	public Long getLogsLen() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			long logLen = jedis.slowlogLen();
			return logLen;
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	@Override
	public String logEmpty() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.slowlogReset();
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	@Override
	public Long dbSize() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			//配置redis服务信息
			Client client = jedis.getClient();
			client.dbSize();
			return client.getIntegerReply();
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

}
