/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysMonitorService;

@Controller
public class SysMonitorController {
	
	@Autowired
	private SysMonitorService sysMonitorService;
	
	/**
	 * 
	     * @Title: queryMonitorInfoMation
	     * @Description: 获取系统信息的业务逻辑实现类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysMonitorController/queryMonitorInfoMation")
	@ResponseBody
	public void queryMonitorInfoMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysMonitorService.queryMonitorInfoMation(inputObject, outputObject);
	}
	
}
