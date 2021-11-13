/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysEveModelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SysEveModelTypeController {

	@Autowired
	private SysEveModelTypeService sysEveModelTypeService;
	
	/**
	 * 
	 * @Title: querySysEveModelTypeList
	 * @Description: 获取系统模板分类列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysEveModelTypeController/querySysEveModelTypeList")
	@ResponseBody
	public void querySysEveModelTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelTypeService.querySysEveModelTypeList(inputObject, outputObject);
	}
	
	/**
	 * 
	 * @Title: insertSysEveModelType
	 * @Description: 新增系统模板分类
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysEveModelTypeController/insertSysEveModelType")
	@ResponseBody
	public void insertSysEveModelType(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelTypeService.insertSysEveModelType(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: delSysEveModelTypeById
	 * @Description: 删除系统模板分类
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysEveModelTypeController/delSysEveModelTypeById")
	@ResponseBody
	public void delSysEveModelTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelTypeService.delSysEveModelTypeById(inputObject, outputObject);
	}

	/**
	 * 
	 * @Title: querySysEveModelTypeById
	 * @Description: 根据id查询系统模板分类详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysEveModelTypeController/querySysEveModelTypeById")
	@ResponseBody
	public void querySysEveModelTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelTypeService.querySysEveModelTypeById(inputObject, outputObject);
	}

	/**
	 * 
	 * @Title: updateSysEveModelTypeById
	 * @Description: 通过id编辑对应的系统模板分类信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysEveModelTypeController/updateSysEveModelTypeById")
	@ResponseBody
	public void updateSysEveModelTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelTypeService.updateSysEveModelTypeById(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: querySysEveModelTypeByParentId
	 * @Description: 通过parentId查找对应的系统模板分类列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysEveModelTypeController/querySysEveModelTypeByParentId")
	@ResponseBody
	public void querySysEveModelTypeByParentId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelTypeService.querySysEveModelTypeByParentId(inputObject, outputObject);
	}
}
