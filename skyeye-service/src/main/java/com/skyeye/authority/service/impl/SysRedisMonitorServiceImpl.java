package com.skyeye.authority.service.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyeye.authority.service.SysRedisMonitorService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.jedis.JedisClient;
import com.skyeye.jedis.JedisClientClusterService;


@Service
public class SysRedisMonitorServiceImpl implements SysRedisMonitorService{
	
	@Autowired
	public JedisClient jedisClient;
	
	@Autowired
	public JedisClientClusterService jedisClientClusterService;
	
	/**
	 * 
	     * @Title: queryRedisInfoList
	     * @Description: 获取redis服务器信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryRedisInfoList(InputObject inputObject, OutputObject outputObject) throws Exception {
		//获取redis服务器信息
		String info = jedisClient.getRedisInfo();
		List<Map<String, Object>> beans = JSONArray.fromObject(info);
		outputObject.setBeans(beans);
	}

	/**
	 * 
	     * @Title: queryRedisLogsList
	     * @Description: 获取redis日志信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRedisLogsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		List<Map<String, Object>> beans = jedisClientClusterService.getClusterNodes();
		outputObject.setBeans(beans);
	}

}
