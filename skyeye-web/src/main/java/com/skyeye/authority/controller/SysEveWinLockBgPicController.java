package com.skyeye.authority.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.authority.service.SysEveWinLockBgPicService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

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
	
}
