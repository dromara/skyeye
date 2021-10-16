/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SchoolStuExamService;

@Controller
public class SchoolStuExamController {
	
	@Autowired
	private SchoolStuExamService schoolStuExamService;
	
	/**
	 * 
	     * @Title: queryMyWaitExamList
	     * @Description: 获取我的待考试试卷列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStuExamController/queryMyWaitExamList")
	@ResponseBody
	public void queryMyWaitExamList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStuExamService.queryMyWaitExamList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMyEndExamList
	     * @Description: 获取我的已考试试卷列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolStuExamController/queryMyEndExamList")
	@ResponseBody
	public void queryMyEndExamList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolStuExamService.queryMyEndExamList(inputObject, outputObject);
	}
	
}
