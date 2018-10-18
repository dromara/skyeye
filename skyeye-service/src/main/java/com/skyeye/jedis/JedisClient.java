package com.skyeye.jedis;


public interface JedisClient {
	
	/**
	 * 
	     * @Title: set
	     * @Description: Redis SET命令用于在Redis键中设置一些字符串值
	     * @param @param key
	     * @param @param value
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public String set(String key, String value);

	/**
	 * 
	     * @Title: get
	     * @Description: 根据key去查询相应的值
	     * @param @param key
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public String get(String key);

	/**
	 * 
	     * @Title: exists
	     * @Description: 判断key在Redis缓存中是否存在
	     * @param @param key
	     * @param @return    参数
	     * @return Boolean    返回类型
	     * @throws
	 */
	public Boolean exists(String key);

	/**
	 * 
	     * @Title: expire
	     * @Description: 设置key的过期时间
	     * @param @param key
	     * @param @param seconds
	     * @param @return    参数
	     * @return Long    返回类型
	     * @throws
	 */
	public Long expire(String key, int seconds);

	/**
	 * 
	     * @Title: ttl
	     * @Description: Redis TTL 命令以秒为单位返回 key 的剩余过期时间
	     * @param @param key
	     * @param @return    参数
	     * @return Long    返回类型
	     * @throws
	 */
	public Long ttl(String key);

	/**
	 * 
	     * @Title: incr
	     * @Description: Redis Incr 命令将 key 中储存的数字值增一
	     * @param @param key
	     * @param @return    参数
	     * @return Long    返回类型
	     * @throws
	 */
	public Long incr(String key);

	/**
	 * 
	     * @Title: hset
	     * @Description:  Redis Hset 命令用于为哈希表中的字段赋值 。 如果哈希表不存在，一个新的哈希表被创建并进行 HSET 操作。如果字段已经存在于哈希表中，旧值将被覆盖。
	     * @param @param key
	     * @param @param field
	     * @param @param value
	     * @param @return    参数
	     * @return Long    返回类型
	     * @throws
	 */
	public Long hset(String key, String field, String value);

	/**
	 * 
	     * @Title: hget
	     * @Description: Redis Hget 命令用于返回哈希表中指定字段的值。
	     * @param @param key
	     * @param @param field
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	public String hget(String key, String field);

	/**
	 * 
	     * @Title: hdel
	     * @Description: Redis Hdel 命令用于删除哈希表 key 中的一个或多个指定字段，不存在的字段将被忽略。
	     * @param @param key
	     * @param @param field
	     * @param @return    参数
	     * @return Long    返回类型
	     * @throws
	 */
	public Long hdel(String key, String... field);
	
	/**
	 * 
	     * @Title: del
	     * @Description: 删除给定的一个 key 不存在的 key 会被忽略。
	     * @param @param key
	     * @param @param field
	     * @param @return    参数
	     * @return Long    返回类型
	     * @throws
	 */
	public Long del(String key);
	
}
