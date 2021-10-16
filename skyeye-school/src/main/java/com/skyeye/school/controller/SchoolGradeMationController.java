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
import com.skyeye.school.service.SchoolGradeMationService;

@Controller
public class SchoolGradeMationController {
	
	@Autowired
	private SchoolGradeMationService schoolGradeMationService;
	
	/**
	 * 
	     * @Title: querySchoolGradeMationList
	     * @Description: 获取年级信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeMationController/querySchoolGradeMationList")
	@ResponseBody
	public void querySchoolGradeMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeMationService.querySchoolGradeMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSchoolGradeMation
	     * @Description: 添加年级信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeMationController/insertSchoolGradeMation")
	@ResponseBody
	public void insertSchoolGradeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeMationService.insertSchoolGradeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSchoolGradeMationById
	     * @Description: 删除年级信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeMationController/deleteSchoolGradeMationById")
	@ResponseBody
	public void deleteSchoolGradeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeMationService.deleteSchoolGradeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolGradeMationToEditById
	     * @Description: 编辑年级信息信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeMationController/querySchoolGradeMationToEditById")
	@ResponseBody
	public void querySchoolGradeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeMationService.querySchoolGradeMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolGradeMationById
	     * @Description: 编辑年级信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeMationController/editSchoolGradeMationById")
	@ResponseBody
	public void editSchoolGradeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeMationService.editSchoolGradeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllGradeMationBySchoolId
	     * @Description: 查询学校中所有的正常年级
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeMationController/queryAllGradeMationBySchoolId")
	@ResponseBody
	public void queryAllGradeMationBySchoolId(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeMationService.queryAllGradeMationBySchoolId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editGradeMationOrderNumToUp
	     * @Description: 年级上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeMationController/editGradeMationOrderNumToUp")
	@ResponseBody
	public void editGradeMationOrderNumToUp(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeMationService.editGradeMationOrderNumToUp(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editGradeMationOrderNumToDown
	     * @Description: 年级下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeMationController/editGradeMationOrderNumToDown")
	@ResponseBody
	public void editGradeMationOrderNumToDown(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeMationService.editGradeMationOrderNumToDown(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolGradeNowYearMationById
	     * @Description: 获取选中的年级是哪一届的以及这一届的班级信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolGradeMationController/querySchoolGradeNowYearMationById")
	@ResponseBody
	public void querySchoolGradeNowYearMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolGradeMationService.querySchoolGradeNowYearMationById(inputObject, outputObject);
	}
	
}
