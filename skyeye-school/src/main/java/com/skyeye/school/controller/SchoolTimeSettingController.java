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
import com.skyeye.school.service.SchoolTimeSettingService;

@Controller
public class SchoolTimeSettingController {
	
	@Autowired
	private SchoolTimeSettingService schoolTimeSettingService;
	
	/**
	 * 
	     * @Title: querySchoolTimeSettingMation
	     * @Description: 获取年级时间设置相关参数
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTimeSettingController/querySchoolTimeSettingMation")
	@ResponseBody
	public void querySchoolTimeSettingMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTimeSettingService.querySchoolTimeSettingMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolTimeSettingMation
	     * @Description: 修改年级时间设置
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolTimeSettingController/editSchoolTimeSettingMation")
	@ResponseBody
	public void editSchoolTimeSettingMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolTimeSettingService.editSchoolTimeSettingMation(inputObject, outputObject);
	}
	
}
