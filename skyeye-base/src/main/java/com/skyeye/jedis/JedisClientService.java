/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.jedis;

import redis.clients.util.Slowlog;

import java.util.List;


public interface JedisClientService {

	/**
	 *
	     * @Title: logEmpty
	     * @Description: 清空日志
	     * @param @return
	     * @throws Exception    参数
	     * @return String    返回类型
	     * @throws
	 */
	String logEmpty(String ip);

	/**
	 *
	     * @Title: set
	     * @Description: Redis SET命令用于在Redis键中设置一些字符串值
	     * @param key
	     * @param value
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	void set(String key, String value);

	/**
	 *
	    * @Title: set
	    * @Description: Redis SET命令用于在Redis键中设置一些字符串值
	    * @param key
	    * @param value
	    * @param seconds 过期时间，单位：秒
	    * @return void    返回类型
	    * @throws
	 */
	void set(String key, String value, int seconds);

	/**
	 *
	     * @Title: get
	     * @Description: 根据key去查询相应的值
	     * @param key
	     * @param @return    参数
	     * @return String    返回类型
	     * @throws
	 */
	String get(String key);

	/**
	 *
	     * @Title: exists
	     * @Description: 判断key在Redis缓存中是否存在
	     * @param key
	     * @param @return    参数
	     * @return Boolean    返回类型
	     * @throws
	 */
	Boolean exists(String key);

	/**
	 *
	     * @Title: expire
	     * @Description: 设置key的过期时间
	     * @param key
	     * @param seconds 单位：秒
	     * @param @return    参数
	     * @return Long    返回类型
	     * @throws
	 */
	boolean expire(String key, int seconds);

	/**
	 *
	     * @Title: incrByData
	     * @Description: Redis Incr 命令将 key 中储存的数字值增idata
	     * @param key
	     * @param @return    参数
	     * @return Long    返回类型
	     * @throws
	 */
	Long incrByData(String key, long idata);

	/**
	 *
	     * @Title: del
	     * @Description: 删除给定的一个 key 不存在的 key 会被忽略。
	     * @param key
	     * @param @return    参数
	     * @return Long    返回类型
	     * @throws
	 */
	boolean del(String key);

	/**
	 *
	     * @Title: getLogs
	     * @Description: 获取日志列表
	     * @param entries
	     * @param @return
	     * @throws Exception    参数
	     * @return List<Slowlog>    返回类型
	     * @throws
	 */
	List<Slowlog> getLogs(long entries);

	/**
	 *
	     * @Title: getLogsLen
	     * @Description: 获取日志条数
	     * @param @return
	     * @throws Exception    参数
	     * @return Long    返回类型
	     * @throws
	 */
	Long getLogsLen();

	/**
	 *
	     * @Title: logEmpty
	     * @Description: 清空日志
	     * @param @return
	     * @throws Exception    参数
	     * @return String    返回类型
	     * @throws
	 */
	String logEmpty();

	/**
	 *
	     * @Title: dbSize
	     * @Description: 获取占用内存大小
	     * @param @return
	     * @throws Exception    参数
	     * @return Long    返回类型
	     * @throws
	 */
	Long dbSize();

	/**
	 *
	     * @Title: delKeys
	     * @Description: 根据前缀进行删除
	     * @param @return
	     * @throws Exception    参数
	     * @return Long    返回类型
	     * @throws
	 */
	void delKeys(String keysPattern);

}
