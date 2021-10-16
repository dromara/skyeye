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
import com.skyeye.eve.service.SchoolUserService;

@Controller
public class SchoolUserController {
	
	@Autowired
	private SchoolUserService schoolUserService;
	
	/**
	 * 
	     * @Title: queryStuMationToLogin
	     * @Description: 手机端学生登录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolUserController/queryStuMationToLogin")
	@ResponseBody
	public void queryStuMationToLogin(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolUserService.queryStuMationToLogin(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryStuUserMation
	     * @Description: 手机端从session中获取学生信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolUserController/queryStuUserMation")
	@ResponseBody
	public void queryStuUserMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolUserService.queryStuUserMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryStuExit
	     * @Description: 手机端注销登录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolUserController/queryStuExit")
	@ResponseBody
	public void queryStuExit(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolUserService.queryStuExit(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryStuUserMationDetailById
	     * @Description: 获取学生信息详细信息-我的名片
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolUserController/queryStuUserMationDetailById")
	@ResponseBody
	public void queryStuUserMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolUserService.queryStuUserMationDetailById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserPassword
	     * @Description: 修改密码-修改密码
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolUserController/editUserPassword")
	@ResponseBody
	public void editUserPassword(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolUserService.editUserPassword(inputObject, outputObject);
	}
	
}
