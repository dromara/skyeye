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
import com.skyeye.school.service.SchoolGradeSubjectService;

@Controller
public class SchoolGradeSubjectController {

	@Autowired
	private SchoolGradeSubjectService schoolGradeSubjectService;
	
	/**
	 * 
	     * @Title: querySchoolGradeSubjectList
	     * @Description: 获取年级技能列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeSubjectController/querySchoolGradeSubjectList")
	@ResponseBody
	public void querySchoolGradeSubjectList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeSubjectService.querySchoolGradeSubjectList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolGradeSubjectMationById
	     * @Description: 获取年级部分信息以及当前拥有的技能列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeSubjectController/querySchoolGradeSubjectMationById")
	@ResponseBody
	public void querySchoolGradeSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeSubjectService.querySchoolGradeSubjectMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolGradeSubjectMation
	     * @Description: 年级科目技能信息绑定
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeSubjectController/editSchoolGradeSubjectMation")
	@ResponseBody
	public void editSchoolGradeSubjectMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeSubjectService.editSchoolGradeSubjectMation(inputObject, outputObject);
	}
	
}
