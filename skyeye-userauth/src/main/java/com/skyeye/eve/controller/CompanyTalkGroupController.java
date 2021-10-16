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
import com.skyeye.eve.service.CompanyTalkGroupService;

@Controller
public class CompanyTalkGroupController {
	
	@Autowired
	private CompanyTalkGroupService companyTalkGroupService;
	
	/**
	 * 
	     * @Title: insertGroupMation
	     * @Description: 添加群组信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyTalkGroupController/insertGroupMation")
	@ResponseBody
	public void queryCodeModelList(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyTalkGroupService.insertGroupMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryGroupInvitationMation
	     * @Description: 获取邀请信息/入群信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyTalkGroupController/queryGroupInvitationMation")
	@ResponseBody
	public void queryGroupInvitationMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyTalkGroupService.queryGroupInvitationMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAgreeInGroupInvitationMation
	     * @Description: 同意入群
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyTalkGroupController/editAgreeInGroupInvitationMation")
	@ResponseBody
	public void editAgreeInGroupInvitationMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyTalkGroupService.editAgreeInGroupInvitationMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRefuseInGroupInvitationMation
	     * @Description: 拒绝入群
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyTalkGroupController/editRefuseInGroupInvitationMation")
	@ResponseBody
	public void editRefuseInGroupInvitationMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyTalkGroupService.editRefuseInGroupInvitationMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryGroupMationList
	     * @Description: 搜索群组列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyTalkGroupController/queryGroupMationList")
	@ResponseBody
	public void queryGroupMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyTalkGroupService.queryGroupMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertGroupMationToTalk
	     * @Description: 申请加入群聊
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyTalkGroupController/insertGroupMationToTalk")
	@ResponseBody
	public void insertGroupMationToTalk(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyTalkGroupService.insertGroupMationToTalk(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryGroupMemberByGroupId
	     * @Description: 获取群成员
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyTalkGroupController/queryGroupMemberByGroupId")
	@ResponseBody
	public void queryGroupMemberByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyTalkGroupService.queryGroupMemberByGroupId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryChatLogByType
	     * @Description: 获取聊天记录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyTalkGroupController/queryChatLogByType")
	@ResponseBody
	public void queryChatLogByType(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyTalkGroupService.queryChatLogByType(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserToExitGroup
	     * @Description: 退出群聊
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyTalkGroupController/editUserToExitGroup")
	@ResponseBody
	public void editUserToExitGroup(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyTalkGroupService.editUserToExitGroup(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCreateToExitGroup
	     * @Description: 解散群聊
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyTalkGroupController/editCreateToExitGroup")
	@ResponseBody
	public void editCreateToExitGroup(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyTalkGroupService.editCreateToExitGroup(inputObject, outputObject);
	}
	
}
