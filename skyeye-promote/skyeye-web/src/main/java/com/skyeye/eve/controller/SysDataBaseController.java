/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysDataBaseService;


@Controller
public class SysDataBaseController {
	
	@Autowired
	private SysDataBaseService sysDataBaseService;
	
	/**
	 * 
	     * @Title: querySysDataBaseList
	     * @Description: 获取数据库信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDataBaseController/querySysDataBaseList")
	@ResponseBody
	public void querySysDataBaseList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDataBaseService.querySysDataBaseList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysDataBaseList
	     * @Description: 获取数据库表名信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDataBaseController/querySysDataBaseSelectList")
	@ResponseBody
	public void querySysDataBaseSelectList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDataBaseService.querySysDataBaseSelectList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysDataBaseList
	     * @Description: 获取数据库表备注信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDataBaseController/querySysDataBaseDescSelectList")
	@ResponseBody
	public void querySysDataBaseDescSelectList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDataBaseService.querySysDataBaseDescSelectList(inputObject, outputObject);
	}
	
}
