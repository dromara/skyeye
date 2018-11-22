package com.skyeye.authority.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.authority.service.SysTAreaService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class SysTAreaController {
	
	@Autowired
	private SysTAreaService sysTAreaService;
	
	/**
	 * 
	     * @Title: querySysTAreaList
	     * @Description: 获取行政区划信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysTAreaController/querySysTAreaList")
	@ResponseBody
	public void querySysTAreaList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysTAreaService.querySysTAreaList(inputObject, outputObject);
	}
	
}
