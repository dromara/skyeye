package com.skyeye.smprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.smprogram.service.SmProjectPageService;

@Controller
public class SmProjectPageController {
	
	@Autowired
	private SmProjectPageService smProjectPageService;
	
	/**
	 * 
	     * @Title: queryProPageMationByProIdList
	     * @Description: 根据项目获取项目内部的页面
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectPageController/queryProPageMationByProIdList")
	@ResponseBody
	public void queryProPageMationByProIdList(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectPageService.queryProPageMationByProIdList(inputObject, outputObject);
	}
}
