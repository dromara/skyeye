package com.skyeye.jedis.service;

public interface JedisClient {
	
	// Redis SET命令用于在Redis键中设置一些字符串值
	public String set(String key, String value);

	// 根据key去查询相应的值
	public String get(String key);

	// 判断key在Redis缓存中是否存在
	public Boolean exists(String key);

	// 设置key的过期时间
	public Long expire(String key, int seconds);

	// Redis TTL 命令以秒为单位返回 key 的剩余过期时间
	public Long ttl(String key);

	// Redis Incr 命令将 key 中储存的数字值增一
	public Long incr(String key);

	/**
	 * Redis Hset 命令用于为哈希表中的字段赋值 。 如果哈希表不存在，一个新的哈希表被创建并进行 HSET 操作。
	 * 如果字段已经存在于哈希表中，旧值将被覆盖。
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public Long hset(String key, String field, String value);

	// Redis Hget 命令用于返回哈希表中指定字段的值。
	public String hget(String key, String field);

	// Redis Hdel 命令用于删除哈希表 key 中的一个或多个指定字段，不存在的字段将被忽略。
	public Long hdel(String key, String... field);
}
