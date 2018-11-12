package com.skyeye.smprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.smprogram.service.SmProjectPageModeService;

@Controller
public class SmProjectPageModeController {
	
	@Autowired
	private SmProjectPageModeService smProjectPageModeService;
	
	/**
	 * 
	     * @Title: queryProPageModeMationByPageIdList
	     * @Description: 根据项目页面获取该页面拥有的组件列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectPageModeController/queryProPageModeMationByPageIdList")
	@ResponseBody
	public void queryProPageModeMationByPageIdList(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectPageModeService.queryProPageModeMationByPageIdList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editProPageModeMationByPageIdList
	     * @Description: 插入项目页面对应的模块内容
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectPageModeController/editProPageModeMationByPageIdList")
	@ResponseBody
	public void editProPageModeMationByPageIdList(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectPageModeService.editProPageModeMationByPageIdList(inputObject, outputObject);
	}
	
}
