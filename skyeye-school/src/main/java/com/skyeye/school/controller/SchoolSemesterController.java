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
import com.skyeye.school.service.SchoolSemesterService;

@Controller
public class SchoolSemesterController {

	@Autowired
	private SchoolSemesterService schoolSemesterService;
	
	/**
	 * 
	     * @Title: querySchoolSemesterList
	     * @Description: 获取学期信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSemesterController/querySchoolSemesterList")
	@ResponseBody
	public void querySchoolSemesterList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSemesterService.querySchoolSemesterList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSchoolSemesterMation
	     * @Description: 新增学期信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSemesterController/insertSchoolSemesterMation")
	@ResponseBody
	public void insertSchoolSemesterMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSemesterService.insertSchoolSemesterMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolSemesterToEditById
	     * @Description: 通过id查询一条学期信息信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSemesterController/querySchoolSemesterToEditById")
	@ResponseBody
	public void querySchoolSemesterToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSemesterService.querySchoolSemesterToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolSemesterById
	     * @Description: 编辑学期信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSemesterController/editSchoolSemesterById")
	@ResponseBody
	public void editSchoolSemesterById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSemesterService.editSchoolSemesterById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSchoolSemesterById
	     * @Description: 删除学期信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSemesterController/deleteSchoolSemesterById")
	@ResponseBody
	public void deleteSchoolSemesterById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSemesterService.deleteSchoolSemesterById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolSemesterListToShow
	     * @Description: 获取学期信息列表展示为select
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSemesterController/querySchoolSemesterListToShow")
	@ResponseBody
	public void querySchoolSemesterListToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSemesterService.querySchoolSemesterListToShow(inputObject, outputObject);
	}
	
}
