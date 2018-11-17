package com.skyeye.jedis.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.alibaba.fastjson.JSON;
import com.skyeye.common.util.ToolUtil;
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
		return null;
	}

	@Override
	public Long getLogsLen() {
		return new Long(1);
	}

	@Override
	public String logEmpty() {
		return "";
	}

	@Override
	public Long dbSize() {
		return new Long(1);
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

	@Override
	public List<Map<String, Object>> getLogs(String ip) {
		Jedis jedis = null;
		try {
			Map<String, JedisPool> jedisPools = jedisCluster.getClusterNodes();
			Iterator<Map.Entry<String, JedisPool>> entries = jedisPools.entrySet().iterator(); 
			while (entries.hasNext()) {
				Entry<String, JedisPool> entry = entries.next(); 
				if(entry.getKey().indexOf(ip) != -1){
					jedis = entry.getValue().getResource();
					long logLen = jedis.slowlogLen();
					List<Slowlog> logList = jedis.slowlogGet(logLen);
					jedis.close();
					List<Map<String, Object>> opList = null;
					Map<String, Object> op  = null;
					boolean flag = false;
					if (logList != null && !logList.isEmpty()) {
						opList = new LinkedList<>();
						for (Slowlog sl : logList) {
							String args = JSON.toJSONString(sl.getArgs());
							if (args.equals("[\"PING\"]") || args.equals("[\"SLOWLOG\",\"get\"]") || args.equals("[\"DBSIZE\"]") || args.equals("[\"INFO\"]")) {
								continue;
							}	
							op = new HashMap<>();
							flag = true;
							op.put("id", sl.getId());
							op.put("executeTime", ToolUtil.getDateStr(sl.getTimeStamp() * 1000));
							op.put("usedTime", sl.getExecutionTime()/1000.0 + "ms");
							op.put("args", args);
							opList.add(op);
						}
					} 
					if (flag) 
						return opList;
					else 
						return null;
				}
			}
		} finally {
		}
		return null;
	}

	@Override
	public String logEmpty(String ip) {
		return null;
	}

	@Override
	public Map<String, Object> dbSize(String ip) {
		Jedis jedis = null;
		try {
			Map<String, JedisPool> jedisPools = jedisCluster.getClusterNodes();
			Iterator<Map.Entry<String, JedisPool>> entries = jedisPools.entrySet().iterator(); 
			while (entries.hasNext()) {
				Entry<String, JedisPool> entry = entries.next(); 
				if(entry.getKey().indexOf(ip) != -1){
					jedis = entry.getValue().getResource();
					//配置redis服务信息
					Client client = jedis.getClient();
					client.dbSize();
					long dbSize = client.getIntegerReply();
					jedis.close();
					Map<String,Object> map = new HashMap<String, Object>();
					map.put("createTime", ToolUtil.getTimeAndToString());
					map.put("dbSize", dbSize);
					return map;
				}
			}
		} finally {
		}
		return null;
	}

}
