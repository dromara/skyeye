/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysRedisMonitorService;
import com.skyeye.jedis.JedisClientService;


@Service
public class SysRedisMonitorServiceImpl implements SysRedisMonitorService{
	
	@Autowired
	public JedisClientService jedisClientService;
	
	
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
		String info = jedisClientService.getRedisInfo();
		List<Map<String, Object>> beans = JSONArray.fromObject(info);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
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
		List<Map<String, Object>> beans = jedisClientService.getClusterNodes();
		for(Map<String, Object> bean : beans){
			bean.put("log", jedisClientService.getLogs(bean.get("ip").toString()));
		}
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

	/**
	 * 
	     * @Title: queryRedisKeysList
	     * @Description: 获取当前数据库中key的数量
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryRedisKeysList(InputObject inputObject, OutputObject outputObject) throws Exception {
		List<Map<String, Object>> beans = jedisClientService.getClusterNodes();
		for(Map<String, Object> bean : beans){
			bean.put("keys", jedisClientService.dbSize(bean.get("ip").toString()));
		}
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}

}
