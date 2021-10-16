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
import com.skyeye.eve.service.SysNoticeService;

@Controller
public class SysNoticeController {
	
	@Autowired
	private SysNoticeService sysNoticeService;
	
	/**
	 * 
	     * @Title: querySysNoticeList
	     * @Description: 获取公告列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/querySysNoticeList")
	@ResponseBody
	public void querySysNoticeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.querySysNoticeList(inputObject, outputObject);
	}
	
	
	/**
	 * 
	     * @Title: insertSysNoticeMation
	     * @Description: 添加公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/insertSysNoticeMation")
	@ResponseBody
	public void insertSysNoticeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.insertSysNoticeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysNoticeById
	     * @Description: 删除公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/deleteSysNoticeById")
	@ResponseBody
	public void deleteSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.deleteSysNoticeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateUpSysNoticeById
	     * @Description: 上线公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/updateUpSysNoticeById")
	@ResponseBody
	public void updateUpSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.updateUpSysNoticeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateDownSysNoticeById
	     * @Description: 下线公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/updateDownSysNoticeById")
	@ResponseBody
	public void updateDownSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.updateDownSysNoticeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectSysNoticeById
	     * @Description: 通过id查找对应的公告信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/selectSysNoticeById")
	@ResponseBody
	public void selectSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.selectSysNoticeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysNoticeMationById
	     * @Description: 编辑公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/editSysNoticeMationById")
	@ResponseBody
	public void editSysNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.editSysNoticeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysNoticeMationOrderNumUpById
	     * @Description: 公告上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/editSysNoticeMationOrderNumUpById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.editSysNoticeMationOrderNumUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysNoticeMationOrderNumDownById
	     * @Description: 公告下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/editSysNoticeMationOrderNumDownById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.editSysNoticeMationOrderNumDownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysNoticeTimeUpById
	     * @Description: 定时上线时间
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/editSysNoticeTimeUpById")
	@ResponseBody
	public void editSysNoticeTimeUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.editSysNoticeTimeUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysNoticeDetailsById
	     * @Description: 公告详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/querySysNoticeDetailsById")
	@ResponseBody
	public void querySysNoticeDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.querySysNoticeDetailsById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUserReceivedSysNotice
	     * @Description: 用户收到的公告
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/queryUserReceivedSysNotice")
	@ResponseBody
	public void queryUserReceivedSysNotice(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.queryUserReceivedSysNotice(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryReceivedSysNoticeDetailsById
	     * @Description: 用户收到的公告详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeController/queryReceivedSysNoticeDetailsById")
	@ResponseBody
	public void queryReceivedSysNoticeDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeService.queryReceivedSysNoticeDetailsById(inputObject, outputObject);
	}
	
}
