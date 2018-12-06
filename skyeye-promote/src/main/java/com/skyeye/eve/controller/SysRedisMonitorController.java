package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysRedisMonitorService;


@Controller
public class SysRedisMonitorController {
	
	@Autowired
	private SysRedisMonitorService sysRedisMonitorService;
	
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
	@RequestMapping("/post/SysRedisMonitorController/queryRedisInfoList")
	@ResponseBody
	public void queryRedisInfoList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysRedisMonitorService.queryRedisInfoList(inputObject, outputObject);
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
	@RequestMapping("/post/SysRedisMonitorController/queryRedisLogsList")
	@ResponseBody
	public void queryRedisLogsList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysRedisMonitorService.queryRedisLogsList(inputObject, outputObject);
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
	@RequestMapping("/post/SysRedisMonitorController/queryRedisKeysList")
	@ResponseBody
	public void queryRedisKeysList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysRedisMonitorService.queryRedisKeysList(inputObject, outputObject);
	}
	
}
