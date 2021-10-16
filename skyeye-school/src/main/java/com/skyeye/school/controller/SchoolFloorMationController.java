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
import com.skyeye.school.service.SchoolFloorMationService;

@Controller
public class SchoolFloorMationController {

	@Autowired
	private SchoolFloorMationService schoolFloorMationService;
	
	/**
	 * 
	     * @Title: querySchoolFloorMationList
	     * @Description: 获取教学楼信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFloorMationController/querySchoolFloorMationList")
	@ResponseBody
	public void querySchoolFloorMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFloorMationService.querySchoolFloorMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSchoolFloorMationMation
	     * @Description: 新增教学楼信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFloorMationController/insertSchoolFloorMationMation")
	@ResponseBody
	public void insertSchoolFloorMationMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFloorMationService.insertSchoolFloorMationMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolFloorMationToEditById
	     * @Description: 通过id查询一条教学楼信息信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFloorMationController/querySchoolFloorMationToEditById")
	@ResponseBody
	public void querySchoolFloorMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFloorMationService.querySchoolFloorMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolFloorMationById
	     * @Description: 编辑教学楼信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFloorMationController/editSchoolFloorMationById")
	@ResponseBody
	public void editSchoolFloorMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFloorMationService.editSchoolFloorMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSchoolFloorMationById
	     * @Description: 删除教学楼信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFloorMationController/deleteSchoolFloorMationById")
	@ResponseBody
	public void deleteSchoolFloorMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFloorMationService.deleteSchoolFloorMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolFloorMationToSelectList
	     * @Description: 获取教学楼信息列表展示为select
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolFloorMationController/querySchoolFloorMationToSelectList")
	@ResponseBody
	public void querySchoolFloorMationToSelectList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolFloorMationService.querySchoolFloorMationToSelectList(inputObject, outputObject);
	}
	
}
