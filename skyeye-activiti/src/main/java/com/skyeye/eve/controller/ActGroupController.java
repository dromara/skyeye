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
import com.skyeye.eve.service.ActGroupService;

@Controller
public class ActGroupController {
	
	@Autowired
	private ActGroupService actGroupService;
	
	/**
	 * 
	     * @Title: insertActGroupMation
	     * @Description: 新增用户组
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActGroupController/insertActGroupMation")
	@ResponseBody
	public void insertActGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		actGroupService.insertActGroupMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectAllActGroupMation
	     * @Description: 遍历所有的用户组
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActGroupController/selectAllActGroupMation")
	@ResponseBody
	public void selectAllActGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		actGroupService.selectAllActGroupMation(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: insertActGroupUserByGroupId
	     * @Description: 给用户组新增用户
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActGroupController/insertActGroupUserByGroupId")
	@ResponseBody
	public void insertActGroupUserByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception{
		actGroupService.insertActGroupUserByGroupId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActGroupNameByGroupId
	     * @Description: 编辑用户组名
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActGroupController/editActGroupNameByGroupId")
	@ResponseBody
	public void editActGroupNameByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception{
		actGroupService.editActGroupNameByGroupId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteActGroupByGroupId
	     * @Description: 删除用户组
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActGroupController/deleteActGroupByGroupId")
	@ResponseBody
	public void deleteActGroupByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception{
		actGroupService.deleteActGroupByGroupId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteActGroupUserByGroupIdAndUserId
	     * @Description: 移除用户组中的某个用户
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActGroupController/deleteActGroupUserByGroupIdAndUserId")
	@ResponseBody
	public void deleteActGroupUserByGroupIdAndUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		actGroupService.deleteActGroupUserByGroupIdAndUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectUserInfoOnActGroup
	     * @Description: 展示用户组的用户信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActGroupController/selectUserInfoOnActGroup")
	@ResponseBody
	public void selectUserInfoOnActGroup(InputObject inputObject, OutputObject outputObject) throws Exception{
		actGroupService.selectUserInfoOnActGroup(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteAllActGroupUserByGroupId
	     * @Description: 一键移除指定用户组下的所有用户
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActGroupController/deleteAllActGroupUserByGroupId")
	@ResponseBody
	public void deleteAllActGroupUserByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception{
		actGroupService.deleteAllActGroupUserByGroupId(inputObject, outputObject);
	}
}
