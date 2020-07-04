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
import com.skyeye.eve.service.SysWorkLogService;

@Controller
public class SysWorkLogController {
	
	@Autowired
	private SysWorkLogService sysWorkLogService;
	
	/**
	 * 
	     * @Title: querySysMenuList
	     * @Description: 获取日志列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkLogController/querySysWorkLogList")
	@ResponseBody
	public void querySysWorkLogList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkLogService.querySysWorkLogList(inputObject, outputObject);
	}
}
