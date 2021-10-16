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
import com.skyeye.school.service.SchoolClassMationService;

@Controller
public class SchoolClassMationController {
	
	@Autowired
	private SchoolClassMationService schoolClassMationService;
	
	/**
	 * 
	     * @Title: querySchoolClassMationList
	     * @Description: 获取班级信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolClassMationController/querySchoolClassMationList")
	@ResponseBody
	public void querySchoolClassMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolClassMationService.querySchoolClassMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSchoolClassMation
	     * @Description: 添加班级信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolClassMationController/insertSchoolClassMation")
	@ResponseBody
	public void insertSchoolClassMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolClassMationService.insertSchoolClassMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSchoolClassMationById
	     * @Description: 删除班级信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolClassMationController/deleteSchoolClassMationById")
	@ResponseBody
	public void deleteSchoolClassMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolClassMationService.deleteSchoolClassMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolClassMationToEditById
	     * @Description: 编辑班级信息信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolClassMationController/querySchoolClassMationToEditById")
	@ResponseBody
	public void querySchoolClassMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolClassMationService.querySchoolClassMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolClassMationById
	     * @Description: 编辑班级信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolClassMationController/editSchoolClassMationById")
	@ResponseBody
	public void editSchoolClassMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolClassMationService.editSchoolClassMationById(inputObject, outputObject);
	}
	
}
