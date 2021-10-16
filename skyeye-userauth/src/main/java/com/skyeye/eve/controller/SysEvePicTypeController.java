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
import com.skyeye.eve.service.SysEvePicTypeService;

@Controller
public class SysEvePicTypeController {

	@Autowired
	private SysEvePicTypeService sysEvePicTypeService;
	
	/**
	 * 
	     * @Title: querySysPicTypeList
	     * @Description: 获取系统图片类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEvePicTypeController/querySysPicTypeList")
	@ResponseBody
	public void querySysPicTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEvePicTypeService.querySysPicTypeList(inputObject, outputObject);
	}
	
	
	/**
	 * 
	     * @Title: insertSysPicTypeMation
	     * @Description: 添加系统图片类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEvePicTypeController/insertSysPicTypeMation")
	@ResponseBody
	public void insertSysPicTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEvePicTypeService.insertSysPicTypeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysPicTypeById
	     * @Description: 删除系统图片类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEvePicTypeController/deleteSysPicTypeById")
	@ResponseBody
	public void deleteSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEvePicTypeService.deleteSysPicTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateUpSysPicTypeById
	     * @Description: 上线系统图片类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEvePicTypeController/updateUpSysPicTypeById")
	@ResponseBody
	public void updateUpSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEvePicTypeService.updateUpSysPicTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateDownSysPicTypeById
	     * @Description: 下线系统图片类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEvePicTypeController/updateDownSysPicTypeById")
	@ResponseBody
	public void updateDownSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEvePicTypeService.updateDownSysPicTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectSysPicTypeById
	     * @Description: 通过id查找对应的图片类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEvePicTypeController/selectSysPicTypeById")
	@ResponseBody
	public void selectSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEvePicTypeService.selectSysPicTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysPicTypeMationById
	     * @Description: 通过id编辑对应的图片类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEvePicTypeController/editSysPicTypeMationById")
	@ResponseBody
	public void editSysPicTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEvePicTypeService.editSysPicTypeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysPicTypeMationOrderNumUpById
	     * @Description: 图片类型上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEvePicTypeController/editSysPicTypeMationOrderNumUpById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEvePicTypeService.editSysPicTypeMationOrderNumUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysPicTypeMationOrderNumDownById
	     * @Description: 图片类型下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEvePicTypeController/editSysPicTypeMationOrderNumDownById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEvePicTypeService.editSysPicTypeMationOrderNumDownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysPicTypeUpStateList
	     * @Description: 获取已经上线的图片类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEvePicTypeController/querySysPicTypeUpStateList")
	@ResponseBody
	public void querySysPicTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEvePicTypeService.querySysPicTypeUpStateList(inputObject, outputObject);
	}

}
