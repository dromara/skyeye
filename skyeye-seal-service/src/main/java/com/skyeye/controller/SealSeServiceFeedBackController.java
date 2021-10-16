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
import com.skyeye.service.SealSeServiceFeedBackService;

@Controller
public class SealSeServiceFeedBackController {
	
	@Autowired
	private SealSeServiceFeedBackService sealSeServiceFeedBackService;
	
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
	@RequestMapping("/post/SealSeServiceFeedBackController/queryFeedBackList")
	@ResponseBody
	public void queryFeedBackList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceFeedBackService.queryFeedBackList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: querySealServiceMationToFeedBack
	 * @Description: 根据工单id获取反馈信息填写时的信息展示
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceFeedBackController/querySealServiceMationToFeedBack")
	@ResponseBody
	public void querySealServiceMationToFeedBack(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceFeedBackService.querySealServiceMationToFeedBack(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: insertFeedBackMation
	 * @Description: 新增情况反馈
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceFeedBackController/insertFeedBackMation")
	@ResponseBody
	public void insertFeedBackMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceFeedBackService.insertFeedBackMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryFeedBackMationToEditById
	 * @Description: 编辑情况反馈时信息回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceFeedBackController/queryFeedBackMationToEditById")
	@ResponseBody
	public void queryFeedBackMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceFeedBackService.queryFeedBackMationToEditById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editFeedBackMationById
	 * @Description: 编辑情况反馈
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceFeedBackController/editFeedBackMationById")
	@ResponseBody
	public void editFeedBackMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceFeedBackService.editFeedBackMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: deleteFeedBackMationById
	 * @Description: 删除情况反馈
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceFeedBackController/deleteFeedBackMationById")
	@ResponseBody
	public void deleteFeedBackMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceFeedBackService.deleteFeedBackMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryFeedBackDetailsMationById
	 * @Description: 情况反馈详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/SealSeServiceFeedBackController/queryFeedBackDetailsMationById")
	@ResponseBody
	public void queryFeedBackDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceFeedBackService.queryFeedBackDetailsMationById(inputObject, outputObject);
	}
	
}
