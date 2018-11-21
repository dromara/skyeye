package com.skyeye.authority.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.authority.service.SysMonitorService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class SysMonitorController {
	
	@Autowired
	private SysMonitorService sysMonitorService;
	
	/**
	 * 
	     * @Title: queryMonitorInfoMation
	     * @Description: 获取系统信息的业务逻辑实现类
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysMonitorController/queryMonitorInfoMation")
	@ResponseBody
	public void queryMonitorInfoMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysMonitorService.queryMonitorInfoMation(inputObject, outputObject);
	}
	
}
