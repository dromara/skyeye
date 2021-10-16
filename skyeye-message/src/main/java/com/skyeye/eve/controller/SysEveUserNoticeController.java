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
import com.skyeye.eve.service.SysEveUserNoticeService;

@Controller
public class SysEveUserNoticeController {
	
	@Autowired
	private SysEveUserNoticeService sysEveUserNoticeService;
	
	/**
	 * 
	     * @Title: getNoticeListByUserId
	     * @Description: 根据用户id获取用户的消息只查询8条
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserNoticeController/getNoticeListByUserId")
	@ResponseBody
	public void getNoticeListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserNoticeService.getNoticeListByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: getAllNoticeListByUserId
	     * @Description: 根据用户id获取用户的消息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserNoticeController/getAllNoticeListByUserId")
	@ResponseBody
	public void getAllNoticeListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserNoticeService.getAllNoticeListByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editNoticeMationById
	     * @Description: 用户阅读消息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserNoticeController/editNoticeMationById")
	@ResponseBody
	public void editNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserNoticeService.editNoticeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteNoticeMationById
	     * @Description: 用户删除消息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserNoticeController/deleteNoticeMationById")
	@ResponseBody
	public void deleteNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserNoticeService.deleteNoticeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editNoticeMationByIds
	     * @Description: 用户批量阅读消息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserNoticeController/editNoticeMationByIds")
	@ResponseBody
	public void editNoticeMationByIds(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserNoticeService.editNoticeMationByIds(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteNoticeMationByIds
	     * @Description: 用户批量删除消息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserNoticeController/deleteNoticeMationByIds")
	@ResponseBody
	public void deleteNoticeMationByIds(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserNoticeService.deleteNoticeMationByIds(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryNoticeMationById
	     * @Description: 获取消息详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserNoticeController/queryNoticeMationById")
	@ResponseBody
	public void queryNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserNoticeService.queryNoticeMationById(inputObject, outputObject);
	}
	
}
