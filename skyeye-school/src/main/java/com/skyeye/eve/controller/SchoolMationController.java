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
import com.skyeye.eve.service.SchoolMationService;

@Controller
public class SchoolMationController {
	
	@Autowired
	private SchoolMationService schoolMationService;
	
	/**
	 * 
	     * @Title: querySchoolMationList
	     * @Description: 获取学校信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolMationController/querySchoolMationList")
	@ResponseBody
	public void querySchoolMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolMationService.querySchoolMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSchoolMation
	     * @Description: 添加学校信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolMationController/insertSchoolMation")
	@ResponseBody
	public void insertSchoolMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolMationService.insertSchoolMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSchoolMationById
	     * @Description: 删除学校信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolMationController/deleteSchoolMationById")
	@ResponseBody
	public void deleteSchoolMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolMationService.deleteSchoolMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolMationToEditById
	     * @Description: 编辑学校信息信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolMationController/querySchoolMationToEditById")
	@ResponseBody
	public void querySchoolMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolMationService.querySchoolMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolMationById
	     * @Description: 编辑学校信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolMationController/editSchoolMationById")
	@ResponseBody
	public void editSchoolMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolMationService.editSchoolMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryOverAllSchoolMationList
	     * @Description: 获取所有的一级学校信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolMationController/queryOverAllSchoolMationList")
	@ResponseBody
	public void queryOverAllSchoolMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolMationService.queryOverAllSchoolMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolListToSelect
	     * @Description: 获取所有学校列表展示为下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolMationController/querySchoolListToSelect")
	@ResponseBody
	public void querySchoolListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolMationService.querySchoolListToSelect(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolPowerListToSelect
	     * @Description: 获取拥有的权限学校列表展示为下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolMationController/querySchoolPowerListToSelect")
	@ResponseBody
	public void querySchoolPowerListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolMationService.querySchoolPowerListToSelect(inputObject, outputObject);
	}
	
}
