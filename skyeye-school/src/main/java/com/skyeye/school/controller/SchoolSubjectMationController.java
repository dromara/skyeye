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
import com.skyeye.school.service.SchoolSubjectMationService;

@Controller
public class SchoolSubjectMationController {

	@Autowired
	private SchoolSubjectMationService schoolSubjectMationService;
	
	/**
	 * 
	     * @Title: querySchoolSubjectMationList
	     * @Description: 获取科目列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSubjectMationController/querySchoolSubjectMationList")
	@ResponseBody
	public void querySchoolSubjectMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSubjectMationService.querySchoolSubjectMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSchoolSubjectMationMation
	     * @Description: 新增科目
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSubjectMationController/insertSchoolSubjectMationMation")
	@ResponseBody
	public void insertSchoolSubjectMationMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSubjectMationService.insertSchoolSubjectMationMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolSubjectMationToEditById
	     * @Description: 通过id查询一条科目信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSubjectMationController/querySchoolSubjectMationToEditById")
	@ResponseBody
	public void querySchoolSubjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSubjectMationService.querySchoolSubjectMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolSubjectMationById
	     * @Description: 编辑科目信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSubjectMationController/editSchoolSubjectMationById")
	@ResponseBody
	public void editSchoolSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSubjectMationService.editSchoolSubjectMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSchoolSubjectMationById
	     * @Description: 删除科目信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSubjectMationController/deleteSchoolSubjectMationById")
	@ResponseBody
	public void deleteSchoolSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSubjectMationService.deleteSchoolSubjectMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolSubjectMationListToShow
	     * @Description: 获取科目信息列表展示为select/chockbox
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSubjectMationController/querySchoolSubjectMationListToShow")
	@ResponseBody
	public void querySchoolSubjectMationListToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSubjectMationService.querySchoolSubjectMationListToShow(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolSubjectMationListToShowByGradeId
	     * @Description: 根据年级获取科目信息列表展示为select/chockbox
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolSubjectMationController/querySchoolSubjectMationListToShowByGradeId")
	@ResponseBody
	public void querySchoolSubjectMationListToShowByGradeId(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolSubjectMationService.querySchoolSubjectMationListToShowByGradeId(inputObject, outputObject);
	}
	
}
