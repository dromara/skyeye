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
import com.skyeye.eve.service.ExExplainToDsFormDisplayTemplateService;

@Controller
public class ExExplainToDsFormDisplayTemplateController {
	
	@Autowired
	private ExExplainToDsFormDisplayTemplateService exExplainToDsFormDisplayTemplateService;
	
	/**
	 * 
	     * @Title: insertExExplainToDsFormDisplayTemplateMation
	     * @Description: 添加动态表单数据展示模板说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToDsFormDisplayTemplateController/insertExExplainToDsFormDisplayTemplateMation")
	@ResponseBody
	public void insertExExplainToDsFormDisplayTemplateMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToDsFormDisplayTemplateService.insertExExplainToDsFormDisplayTemplateMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryExExplainToDsFormDisplayTemplateMation
	     * @Description: 编辑动态表单数据展示模板说明信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToDsFormDisplayTemplateController/queryExExplainToDsFormDisplayTemplateMation")
	@ResponseBody
	public void queryExExplainToDsFormDisplayTemplateMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToDsFormDisplayTemplateService.queryExExplainToDsFormDisplayTemplateMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editExExplainToDsFormDisplayTemplateMationById
	     * @Description: 编辑动态表单数据展示模板说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToDsFormDisplayTemplateController/editExExplainToDsFormDisplayTemplateMationById")
	@ResponseBody
	public void editExExplainToDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToDsFormDisplayTemplateService.editExExplainToDsFormDisplayTemplateMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryExExplainToDsFormDisplayTemplateMationToShow
	     * @Description: 获取动态表单数据展示模板说明信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToDsFormDisplayTemplateController/queryExExplainToDsFormDisplayTemplateMationToShow")
	@ResponseBody
	public void queryExExplainToDsFormDisplayTemplateMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToDsFormDisplayTemplateService.queryExExplainToDsFormDisplayTemplateMationToShow(inputObject, outputObject);
	}
	
}
