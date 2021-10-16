/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealServicePhoneService;

@Controller
public class SealServicePhoneController {
	
	@Autowired
	private SealServicePhoneService sealServicePhoneService;
	
	/**
	 * 
	     * @Title: queryNumberInEveryState
	     * @Description: 手机端查询不同状态下的工单数量
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SealServicePhoneController/queryNumberInEveryState")
	@ResponseBody
	public void queryNumberInEveryState(InputObject inputObject, OutputObject outputObject) throws Exception{
		sealServicePhoneService.queryNumberInEveryState(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSealSeServiceWaitToSignonMation
	     * @Description: 手机端签到
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SealServicePhoneController/insertSealSeServiceWaitToSignonMation")
	@ResponseBody
	public void insertSealSeServiceWaitToSignonMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sealServicePhoneService.insertSealSeServiceWaitToSignonMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryFeedBackList
	 * @Description: 根据工单id获取情况反馈列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealServicePhoneController/queryFeedBackList")
	@ResponseBody
	public void queryFeedBackList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealServicePhoneService.queryFeedBackList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryAllPartsList
	 * @Description: 获取所有配件信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealServicePhoneController/queryAllPartsList")
	@ResponseBody
	public void queryAllPartsList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealServicePhoneService.queryAllPartsList(inputObject, outputObject);
	}
	
}
