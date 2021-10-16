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
import com.skyeye.school.service.SchoolStudentMationService;

@Controller
public class SchoolStudentMationController {
	
	@Autowired
	private SchoolStudentMationService schoolStudentMationService;
	
	/**
	 * 
	     * @Title: querySchoolStudentMationList
	     * @Description: 获取在校学生信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStudentMationController/querySchoolStudentMationList")
	@ResponseBody
	public void querySchoolStudentMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStudentMationService.querySchoolStudentMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSchoolStudentMation
	     * @Description: 新增学生信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStudentMationController/insertSchoolStudentMation")
	@ResponseBody
	public void insertSchoolStudentMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStudentMationService.insertSchoolStudentMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryNotDividedIntoClassesSchoolStudentMationList
	     * @Description: 获取未分班的在校学生信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStudentMationController/queryNotDividedIntoClassesSchoolStudentMationList")
	@ResponseBody
	public void queryNotDividedIntoClassesSchoolStudentMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStudentMationService.queryNotDividedIntoClassesSchoolStudentMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolStudentMationToOperatorById
	     * @Description: 获取学生部分信息展示供其他操作时查看
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStudentMationController/querySchoolStudentMationToOperatorById")
	@ResponseBody
	public void querySchoolStudentMationToOperatorById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStudentMationService.querySchoolStudentMationToOperatorById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAssignmentClassByStuId
	     * @Description: 未分班学生分班
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStudentMationController/editAssignmentClassByStuId")
	@ResponseBody
	public void editAssignmentClassByStuId(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStudentMationService.editAssignmentClassByStuId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolStudentMationToEditById
	     * @Description: 获取学生信息用来编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStudentMationController/querySchoolStudentMationToEditById")
	@ResponseBody
	public void querySchoolStudentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStudentMationService.querySchoolStudentMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolStudentMationById
	     * @Description: 编辑学生信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStudentMationController/editSchoolStudentMationById")
	@ResponseBody
	public void editSchoolStudentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStudentMationService.editSchoolStudentMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolStudentMationDetailById
	     * @Description: 获取学生信息详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStudentMationController/querySchoolStudentMationDetailById")
	@ResponseBody
	public void querySchoolStudentMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStudentMationService.querySchoolStudentMationDetailById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: exportSchoolStudentMationModel
	     * @Description: 导出学生模板
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStudentMationController/exportSchoolStudentMationModel")
	@ResponseBody
	public void exportSchoolStudentMationModel(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStudentMationService.exportSchoolStudentMationModel(inputObject, outputObject);
	}
	
}
