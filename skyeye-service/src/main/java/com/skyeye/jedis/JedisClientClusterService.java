package com.skyeye.jedis;

import java.util.List;
import java.util.Map;

public interface JedisClientClusterService {
	
	public List<Map<String, Object>> getClusterNodes() throws Exception;
	
	/**
	 * 
	     * @Title: getLogs
	     * @Description: 获取日志列表
	     * @param @param entries
	     * @param @return
	     * @param @throws Exception    参数
	     * @return List<Map<String, Object>>    返回类型
	     * @throws
	 */
	public List<Map<String, Object>> getLogs(String ip);

	/**
	 * 
	     * @Title: logEmpty
	     * @Description: 清空日志
	     * @param @return
	     * @param @throws Exception    参数
	     * @return String    返回类型
	     * @throws
	 */
	public String logEmpty(String ip);

	/**
	 * 
	     * @Title: dbSize
	     * @Description: 获取占用内存大小
	     * @param @return
	     * @param @throws Exception    参数
	     * @return Long    返回类型
	     * @throws
	 */
	public Long dbSize(String ip);
	
}
