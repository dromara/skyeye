package com.skyeye.jedis.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.skyeye.jedis.JedisClient;
import com.skyeye.jedis.JedisClientClusterService;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Slowlog;

/**
 * 
     * @ClassName: JedisClientCluster
     * @Description: redis集群
     * @author 卫志强
     * @date 2018年11月17日
     *
 */
public class JedisClientCluster implements JedisClient, JedisClientClusterService {
	
	@Autowired
	private JedisCluster jedisCluster;
	
	@Value("${redis.ip1}")  
    private String redisIp1;
	
	@Value("${redis.ip2}")  
    private String redisIp2;
	
	@Value("${redis.ip3}")  
    private String redisIp3;
	
	@Value("${redis.ip4}")  
    private String redisIp4;
	
	@Value("${redis.ip5}")  
    private String redisIp5;
	
	@Value("${redis.ip6}")  
    private String redisIp6;

	@Override
	public String set(String key, String value) {
		return jedisCluster.set(key, value);
	}

	@Override
	public String get(String key) {
		return jedisCluster.get(key);
	}

	@Override
	public Boolean exists(String key) {
		return jedisCluster.exists(key);
	}

	@Override
	public Long expire(String key, int seconds) {
		return jedisCluster.expire(key, seconds);
	}

	@Override
	public Long ttl(String key) {
		return jedisCluster.ttl(key);
	}

	@Override
	public Long incr(String key) {
		return jedisCluster.incr(key);
	}

	@Override
	public Long hset(String key, String field, String value) {
		return jedisCluster.hset(key, field, value);
	}

	@Override
	public String hget(String key, String field) {
		return jedisCluster.hget(key, field);
	}

	@Override
	public Long hdel(String key, String... field) {
		return jedisCluster.hdel(key, field);
	}

	@Override
	public Long del(String key) {
        return jedisCluster.del(key);
	}
	
	@Override
	public String getRedisInfo() {
		Jedis jedis = null;
		try {
			Map<String, JedisPool> jedisPools = jedisCluster.getClusterNodes();
			List<Map<String, Object>> ridMationList = new ArrayList<>();
			Iterator<Map.Entry<String, JedisPool>> entries = jedisPools.entrySet().iterator(); 
			while (entries.hasNext()) {
				Entry<String, JedisPool> entry = entries.next(); 
				if(entry.getKey().indexOf(redisIp1) != -1 || entry.getKey().indexOf(redisIp2) != -1
						|| entry.getKey().indexOf(redisIp3) != -1 || entry.getKey().indexOf(redisIp4) != -1
						|| entry.getKey().indexOf(redisIp5) != -1 || entry.getKey().indexOf(redisIp6) != -1){
					jedis = entry.getValue().getResource();
					Client client = jedis.getClient();
					client.info();
					String info = client.getBulkReply();
					jedis.close();
					List<Map<String, Object>> ridList = new ArrayList<>();
					Map<String, Object> redisMation = new HashMap<>();
					String[] strs = info.split("\n");
					Map<String, Object> bean = null;
					if (strs != null && strs.length > 0) {
						for (int i = 0; i < strs.length; i++) {
							String[] str = strs[i].split(":");
							if (str != null && str.length > 1) {
								bean = new HashMap<>();
								bean.put("key", str[0]);
								bean.put("value", str[1].replace("\n", "").replace("\r", ""));
								ridList.add(bean);
							}
						}
						redisMation.put("ip", entry.getKey());
						redisMation.put("mation", ridList);
						ridMationList.add(redisMation);
					}
				}
			}
			return JSONArray.fromObject(ridMationList).toString();
		} finally {
		}
	}

	@Override
	public List<Slowlog> getLogs(long entries) {
		Jedis jedis = null;
		try {
//			jedis = jedisPool.getResource();
//			List<Slowlog> logList = jedis.slowlogGet(entries);
			return null;
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	@Override
	public Long getLogsLen() {
		Jedis jedis = null;
		try {
//			jedis = jedisPool.getResource();
//			long logLen = jedis.slowlogLen();
			return new Long(10);
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	@Override
	public String logEmpty() {
		Jedis jedis = null;
		try {
//			jedis = jedisPool.getResource();
//			return jedis.slowlogReset();
			return "";
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	@Override
	public Long dbSize() {
		Jedis jedis = null;
		try {
//			jedis = jedisPool.getResource();
//			//配置redis服务信息
//			Client client = jedis.getClient();
//			client.dbSize();
//			return client.getIntegerReply();
			return new Long(10);
		} finally {
			// 返还到连接池
			jedis.close();
		}
	}

	@Override
	public List<Map<String, Object>> getClusterNodes() throws Exception {
		Map<String, JedisPool> jedisPools = jedisCluster.getClusterNodes();
		List<Map<String, Object>> ridPoolList = new ArrayList<>();
		Iterator<Map.Entry<String, JedisPool>> entries = jedisPools.entrySet().iterator(); 
		while (entries.hasNext()) {
			Entry<String, JedisPool> entry = entries.next(); 
			if(entry.getKey().indexOf(redisIp1) != -1 || entry.getKey().indexOf(redisIp2) != -1
					|| entry.getKey().indexOf(redisIp3) != -1 || entry.getKey().indexOf(redisIp4) != -1
					|| entry.getKey().indexOf(redisIp5) != -1 || entry.getKey().indexOf(redisIp6) != -1){
				Map<String, Object> redisPoolMation = new HashMap<>();
				redisPoolMation.put("ip", entry.getKey());
				ridPoolList.add(redisPoolMation);
			}
		}
		return ridPoolList;
	}

}
