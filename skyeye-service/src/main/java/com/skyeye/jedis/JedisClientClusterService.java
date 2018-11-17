package com.skyeye.jedis;

import java.util.List;
import java.util.Map;

import redis.clients.jedis.JedisPool;

public interface JedisClientClusterService {
	
	public List<Map<String, Object>> getClusterNodes() throws Exception;
	
}
