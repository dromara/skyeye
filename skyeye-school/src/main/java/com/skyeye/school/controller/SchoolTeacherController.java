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
import com.skyeye.school.service.SchoolTeacherService;

@Controller
public class SchoolTeacherController {

	@Autowired
	private SchoolTeacherService schoolTeacherService;
	
	/**
	 * 
	     * @Title: querySchoolTeacherList
	     * @Description: 获取教师列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTeacherController/querySchoolTeacherList")
	@ResponseBody
	public void querySchoolTeacherList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTeacherService.querySchoolTeacherList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolTeacherToTableList
	     * @Description: 获取教师列表供table表格选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTeacherController/querySchoolTeacherToTableList")
	@ResponseBody
	public void querySchoolTeacherToTableList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTeacherService.querySchoolTeacherToTableList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolTeacherListByStaffIds
	     * @Description: 根据staffId串获取教师列表详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTeacherController/querySchoolTeacherListByStaffIds")
	@ResponseBody
	public void querySchoolTeacherListByStaffIds(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTeacherService.querySchoolTeacherListByStaffIds(inputObject, outputObject);
	}
	
}
