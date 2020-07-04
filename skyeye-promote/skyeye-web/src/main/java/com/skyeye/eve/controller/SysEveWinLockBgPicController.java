/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysEveWinLockBgPicService;

@Controller
public class SysEveWinLockBgPicController {
	
	@Autowired
	private SysEveWinLockBgPicService sysEveWinLockBgPicService;
	
	/**
	 * 
	     * @Title: querySysEveWinLockBgPicList
	     * @Description: 获取win系统锁屏桌面图片列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinLockBgPicController/querySysEveWinLockBgPicList")
	@ResponseBody
	public void querySysEveWinLockBgPicList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinLockBgPicService.querySysEveWinLockBgPicList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysEveWinLockBgPicMation
	     * @Description: 添加win系统锁屏桌面图片信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinLockBgPicController/insertSysEveWinLockBgPicMation")
	@ResponseBody
	public void insertSysEveWinLockBgPicMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinLockBgPicService.insertSysEveWinLockBgPicMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysEveWinLockBgPicMationById
	     * @Description: 删除win系统锁屏桌面图片信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinLockBgPicController/deleteSysEveWinLockBgPicMationById")
	@ResponseBody
	public void deleteSysEveWinLockBgPicMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinLockBgPicService.deleteSysEveWinLockBgPicMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysEveWinBgPicMationByCustom
	     * @Description: 用户自定义上传win系统锁屏桌面图片信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinLockBgPicController/insertSysEveWinBgPicMationByCustom")
	@ResponseBody
	public void insertSysEveWinBgPicMationByCustom(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinLockBgPicService.insertSysEveWinBgPicMationByCustom(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysEveWinBgPicCustomList
	     * @Description: 获取win系统锁屏桌面图片列表用户自定义
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinLockBgPicController/querySysEveWinBgPicCustomList")
	@ResponseBody
	public void querySysEveWinBgPicCustomList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinLockBgPicService.querySysEveWinBgPicCustomList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysEveWinBgPicMationCustomById
	     * @Description: 删除win系统锁屏桌面图片信息用户自定义
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinLockBgPicController/deleteSysEveWinBgPicMationCustomById")
	@ResponseBody
	public void deleteSysEveWinBgPicMationCustomById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinLockBgPicService.deleteSysEveWinBgPicMationCustomById(inputObject, outputObject);
	}
	
}
