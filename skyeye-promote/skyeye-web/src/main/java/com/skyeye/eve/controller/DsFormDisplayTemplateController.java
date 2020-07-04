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
import com.skyeye.eve.service.DsFormDisplayTemplateService;

@Controller
public class DsFormDisplayTemplateController {
	
	@Autowired
	private DsFormDisplayTemplateService dsFormDisplayTemplateService;
	
	/**
	 * 
	     * @Title: queryDsFormDisplayTemplateList
	     * @Description: 获取动态表单数据展示模板列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormDisplayTemplateController/queryDsFormDisplayTemplateList")
	@ResponseBody
	public void queryDsFormDisplayTemplateList(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormDisplayTemplateService.queryDsFormDisplayTemplateList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertDsFormDisplayTemplateMation
	     * @Description: 添加动态表单数据展示模板信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormDisplayTemplateController/insertDsFormDisplayTemplateMation")
	@ResponseBody
	public void insertDsFormDisplayTemplateMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormDisplayTemplateService.insertDsFormDisplayTemplateMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteDsFormDisplayTemplateMationById
	     * @Description: 删除动态表单数据展示模板信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormDisplayTemplateController/deleteDsFormDisplayTemplateMationById")
	@ResponseBody
	public void deleteDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormDisplayTemplateService.deleteDsFormDisplayTemplateMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDsFormDisplayTemplateMationToEditById
	     * @Description: 编辑动态表单数据展示模板信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormDisplayTemplateController/queryDsFormDisplayTemplateMationToEditById")
	@ResponseBody
	public void queryDsFormDisplayTemplateMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormDisplayTemplateService.queryDsFormDisplayTemplateMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editDsFormDisplayTemplateMationById
	     * @Description: 编辑动态表单数据展示模板信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormDisplayTemplateController/editDsFormDisplayTemplateMationById")
	@ResponseBody
	public void editDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormDisplayTemplateService.editDsFormDisplayTemplateMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDisplayTemplateListToShow
	     * @Description: 获取动态表单数据展示模板
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormDisplayTemplateController/queryDisplayTemplateListToShow")
	@ResponseBody
	public void queryDisplayTemplateListToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormDisplayTemplateService.queryDisplayTemplateListToShow(inputObject, outputObject);
	}
	
}
