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
import com.skyeye.service.UserPhoneService;

@Controller
public class UserPhoneController {
	
	@Autowired
	private UserPhoneService userPhoneService;
	
	/**
	 * 
	     * @Title: queryPhoneToLogin
	     * @Description: 手机端用户登录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserPhoneController/queryPhoneToLogin")
	@ResponseBody
	public void queryPhoneToLogin(InputObject inputObject, OutputObject outputObject) throws Exception{
		userPhoneService.queryPhoneToLogin(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryPhoneUserMation
	     * @Description: 手机端从session中获取用户信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserPhoneController/queryPhoneUserMation")
	@ResponseBody
	public void queryPhoneUserMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		userPhoneService.queryPhoneUserMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryPhoneUserMenuAuth
	     * @Description: 手机端从session中获取菜单权限信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserPhoneController/queryPhoneUserMenuAuth")
	@ResponseBody
	public void queryPhoneUserMenuAuth(InputObject inputObject, OutputObject outputObject) throws Exception{
		userPhoneService.queryPhoneUserMenuAuth(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryPhoneToExit
	     * @Description: 手机端注销登录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserPhoneController/queryPhoneToExit")
	@ResponseBody
	public void queryPhoneToExit(InputObject inputObject, OutputObject outputObject) throws Exception{
		userPhoneService.queryPhoneToExit(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUserMationByOpenId
	     * @Description: 根据openId获取用户信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserPhoneController/queryUserMationByOpenId")
	@ResponseBody
	public void queryUserMationByOpenId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userPhoneService.queryUserMationByOpenId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertUserMationByOpenId
	     * @Description: openId绑定用户信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserPhoneController/insertUserMationByOpenId")
	@ResponseBody
	public void insertUserMationByOpenId(InputObject inputObject, OutputObject outputObject) throws Exception{
		userPhoneService.insertUserMationByOpenId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllPeopleToTree
	     * @Description: 人员选择获取所有公司和人
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/UserPhoneController/queryAllPeopleToTree")
	@ResponseBody
	public void queryAllPeopleToTree(InputObject inputObject, OutputObject outputObject) throws Exception{
		userPhoneService.queryAllPeopleToTree(inputObject, outputObject);
	}
	
}
