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
import com.skyeye.eve.service.MyAgencyService;

@Controller
public class MyAgencyController {
	
	@Autowired
	private MyAgencyService myAgencyService;
	
	/**
	 * 
	     * @Title: queryMyAgencyList
	     * @Description: 获取我的代办列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyAgencyController/queryMyAgencyList")
	@ResponseBody
	public void queryMyAgencyList(InputObject inputObject, OutputObject outputObject) throws Exception{
		myAgencyService.queryMyAgencyList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteMyAgencyList
	     * @Description: 取消代办提醒
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyAgencyController/deleteMyAgencyList")
	@ResponseBody
	public void deleteMyAgencyList(InputObject inputObject, OutputObject outputObject) throws Exception{
		myAgencyService.deleteMyAgencyList(inputObject, outputObject);
	}
	
}
