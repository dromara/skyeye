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
import com.skyeye.school.service.MySchoolTaskService;

@Controller
public class MySchoolTaskController {
	
	@Autowired
	private MySchoolTaskService mySchoolTaskService;
	
	/**
	 * 
	     * @Title: queryMyNowLeadClassList
	     * @Description: 获取我当前带领的班级列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MySchoolTaskController/queryMyNowLeadClassList")
	@ResponseBody
	public void queryMyNowLeadClassList(InputObject inputObject, OutputObject outputObject) throws Exception{
		mySchoolTaskService.queryMyNowLeadClassList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMyWaitMarkingList
	     * @Description: 获取我的待阅卷列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MySchoolTaskController/queryMyWaitMarkingList")
	@ResponseBody
	public void queryMyWaitMarkingList(InputObject inputObject, OutputObject outputObject) throws Exception{
		mySchoolTaskService.queryMyWaitMarkingList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMyEndMarkingList
	     * @Description: 获取我的已阅卷列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MySchoolTaskController/queryMyEndMarkingList")
	@ResponseBody
	public void queryMyEndMarkingList(InputObject inputObject, OutputObject outputObject) throws Exception{
		mySchoolTaskService.queryMyEndMarkingList(inputObject, outputObject);
	}
	
}
