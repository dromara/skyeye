/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.EmailSendModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmailSendModelController {
	
	@Autowired
	private EmailSendModelService emailSendModelService;
	
	/**
	 * 
	 * @Title: queryEmailSendModelList
	 * @Description: 可根据标题模糊+收件人分页获取邮箱发送模板列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/EmailSendModel/queryEmailSendModelList")
	@ResponseBody
	public void queryEmailSendModelList(InputObject inputObject, OutputObject outputObject) throws Exception{
		emailSendModelService.queryEmailSendModelList(inputObject, outputObject);
	}
	
	/**
	 * 
	 * @Title: insertEmailSendModel
	 * @Description: 用户新增邮件发送模板
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/EmailSendModel/insertEmailSendModel")
	@ResponseBody
	public void insertEmailSendModel(InputObject inputObject, OutputObject outputObject) throws Exception{
		emailSendModelService.insertEmailSendModel(inputObject, outputObject);
	}
	
	/**
	 * 
	 * @Title: queryEmailSendModelInfoById
	 * @Description: 根据邮箱模板id获取邮件模板详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/EmailSendModel/queryEmailSendModelInfoById")
	@ResponseBody
	public void queryEmailSendModelInfoById(InputObject inputObject, OutputObject outputObject) throws Exception{
		emailSendModelService.queryEmailSendModelInfoById(inputObject, outputObject);
	}
	
	/**
	 * 
	 * @Title: delEmailSendModelById
	 * @Description: 根据邮件模板id删除该模板
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/EmailSendModel/delEmailSendModelById")
	@ResponseBody
	public void delEmailSendModelById(InputObject inputObject, OutputObject outputObject) throws Exception{
		emailSendModelService.delEmailSendModelById(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: updateEmailSendModelById
	 * @Description: 根据邮件模板id更新模板内容
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/EmailSendModel/updateEmailSendModelById")
	@ResponseBody
	public void updateEmailSendModelById(InputObject inputObject, OutputObject outputObject) throws Exception{
		emailSendModelService.updateEmailSendModelById(inputObject, outputObject);
	}

}
