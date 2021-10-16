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
import com.skyeye.school.service.SchoolTeacherSubjectService;

@Controller
public class SchoolTeacherSubjectController {

	@Autowired
	private SchoolTeacherSubjectService schoolTeacherSubjectService;
	
	/**
	 * 
	     * @Title: querySchoolTeacherSubjectList
	     * @Description: 获取教师技能列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTeacherSubjectController/querySchoolTeacherSubjectList")
	@ResponseBody
	public void querySchoolTeacherSubjectList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTeacherSubjectService.querySchoolTeacherSubjectList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolTeacherSubjectMationById
	     * @Description: 获取教师部分信息以及当前拥有的技能列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTeacherSubjectController/querySchoolTeacherSubjectMationById")
	@ResponseBody
	public void querySchoolTeacherSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTeacherSubjectService.querySchoolTeacherSubjectMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolTeacherSubjectMation
	     * @Description: 教师科目技能信息绑定
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTeacherSubjectController/editSchoolTeacherSubjectMation")
	@ResponseBody
	public void editSchoolTeacherSubjectMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTeacherSubjectService.editSchoolTeacherSubjectMation(inputObject, outputObject);
	}
	
}
