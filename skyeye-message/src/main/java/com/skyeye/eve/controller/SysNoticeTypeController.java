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
import com.skyeye.eve.service.SysNoticeTypeService;

@Controller
public class SysNoticeTypeController {

	@Autowired
	private SysNoticeTypeService sysNoticeTypeService;
	
	/**
	 * 
	     * @Title: querySysNoticeTypeList
	     * @Description: 获取公告类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/querySysNoticeTypeList")
	@ResponseBody
	public void querySysNoticeTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.querySysNoticeTypeList(inputObject, outputObject);
	}
	
	
	/**
	 * 
	     * @Title: insertSysNoticeTypeMation
	     * @Description: 添加公告类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/insertSysNoticeTypeMation")
	@ResponseBody
	public void insertSysNoticeTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.insertSysNoticeTypeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysNoticeTypeById
	     * @Description: 删除公告类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/deleteSysNoticeTypeById")
	@ResponseBody
	public void deleteSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.deleteSysNoticeTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateUpSysNoticeTypeById
	     * @Description: 上线公告类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/updateUpSysNoticeTypeById")
	@ResponseBody
	public void updateUpSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.updateUpSysNoticeTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateDownSysNoticeTypeById
	     * @Description: 下线公告类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/updateDownSysNoticeTypeById")
	@ResponseBody
	public void updateDownSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.updateDownSysNoticeTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectSysNoticeTypeById
	     * @Description: 通过id查找对应的公告类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/selectSysNoticeTypeById")
	@ResponseBody
	public void selectSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.selectSysNoticeTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysNoticeTypeMationById
	     * @Description: 编辑公告类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/editSysNoticeTypeMationById")
	@ResponseBody
	public void editSysNoticeTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.editSysNoticeTypeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysNoticeTypeMationOrderNumUpById
	     * @Description: 公告类型上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/editSysNoticeTypeMationOrderNumUpById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.editSysNoticeTypeMationOrderNumUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysNoticeTypeMationOrderNumDownById
	     * @Description: 公告类型下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/editSysNoticeTypeMationOrderNumDownById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.editSysNoticeTypeMationOrderNumDownById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: queryFirstSysNoticeTypeUpStateList
	     * @Description: 获取已经上线的一级类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/queryFirstSysNoticeTypeUpStateList")
	@ResponseBody
	public void queryFirstSysNoticeTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.queryFirstSysNoticeTypeUpStateList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllFirstSysNoticeTypeStateList
	     * @Description: 获取所有的一级类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/queryAllFirstSysNoticeTypeStateList")
	@ResponseBody
	public void queryAllFirstSysNoticeTypeStateList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.queryAllFirstSysNoticeTypeStateList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySecondSysNoticeTypeUpStateList
	     * @Description: 获取上线的一级类型对应的上线的二级类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysNoticeTypeController/querySecondSysNoticeTypeUpStateList")
	@ResponseBody
	public void querySecondSysNoticeTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysNoticeTypeService.querySecondSysNoticeTypeUpStateList(inputObject, outputObject);
	}
}
