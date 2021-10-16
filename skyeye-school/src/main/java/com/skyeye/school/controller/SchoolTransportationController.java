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
import com.skyeye.school.service.SchoolTransportationService;

@Controller
public class SchoolTransportationController {

	@Autowired
	private SchoolTransportationService schoolTransportationService;
	
	/**
	 * 
	     * @Title: querySchoolTransportationList
	     * @Description: 获取交通方式列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTransportationController/querySchoolTransportationList")
	@ResponseBody
	public void querySchoolTransportationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTransportationService.querySchoolTransportationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSchoolTransportationMation
	     * @Description: 新增交通方式
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTransportationController/insertSchoolTransportationMation")
	@ResponseBody
	public void insertSchoolTransportationMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTransportationService.insertSchoolTransportationMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolTransportationToEditById
	     * @Description: 通过id查询一条交通方式信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTransportationController/querySchoolTransportationToEditById")
	@ResponseBody
	public void querySchoolTransportationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTransportationService.querySchoolTransportationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolTransportationById
	     * @Description: 编辑交通方式信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTransportationController/editSchoolTransportationById")
	@ResponseBody
	public void editSchoolTransportationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTransportationService.editSchoolTransportationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSchoolTransportationById
	     * @Description: 删除交通方式信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTransportationController/deleteSchoolTransportationById")
	@ResponseBody
	public void deleteSchoolTransportationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTransportationService.deleteSchoolTransportationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolTransportationListToShow
	     * @Description: 获取交通方式信息列表展示为select/chockbox
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTransportationController/querySchoolTransportationListToShow")
	@ResponseBody
	public void querySchoolTransportationListToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTransportationService.querySchoolTransportationListToShow(inputObject, outputObject);
	}
	
}
