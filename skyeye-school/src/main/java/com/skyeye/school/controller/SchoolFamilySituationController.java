/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.service.SchoolFamilySituationService;

@Controller
public class SchoolFamilySituationController {

	@Autowired
	private SchoolFamilySituationService schoolFamilySituationService;
	
	/**
	 * 
	     * @Title: querySchoolFamilySituationList
	     * @Description: 获取家庭情况列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFamilySituationController/querySchoolFamilySituationList")
	@ResponseBody
	public void querySchoolFamilySituationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFamilySituationService.querySchoolFamilySituationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSchoolFamilySituationMation
	     * @Description: 新增家庭情况
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFamilySituationController/insertSchoolFamilySituationMation")
	@ResponseBody
	public void insertSchoolFamilySituationMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFamilySituationService.insertSchoolFamilySituationMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolFamilySituationToEditById
	     * @Description: 通过id查询一条家庭情况信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFamilySituationController/querySchoolFamilySituationToEditById")
	@ResponseBody
	public void querySchoolFamilySituationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFamilySituationService.querySchoolFamilySituationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolFamilySituationById
	     * @Description: 编辑家庭情况信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFamilySituationController/editSchoolFamilySituationById")
	@ResponseBody
	public void editSchoolFamilySituationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFamilySituationService.editSchoolFamilySituationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSchoolFamilySituationById
	     * @Description: 删除家庭情况信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFamilySituationController/deleteSchoolFamilySituationById")
	@ResponseBody
	public void deleteSchoolFamilySituationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFamilySituationService.deleteSchoolFamilySituationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolFamilySituationListToShow
	     * @Description: 获取家庭情况信息列表展示为select/chockbox
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFamilySituationController/querySchoolFamilySituationListToShow")
	@ResponseBody
	public void querySchoolFamilySituationListToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFamilySituationService.querySchoolFamilySituationListToShow(inputObject, outputObject);
	}
	
}
