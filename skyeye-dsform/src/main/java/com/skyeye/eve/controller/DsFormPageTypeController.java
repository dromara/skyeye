/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.DsFormPageTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DsFormPageTypeController {

	@Autowired
	private DsFormPageTypeService dsFormPageTypeService;
	
	/**
	 * 
	 * @Title: queryDsFormPageTypeList
	 * @Description: 获取动态表单页面分类列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/DsFormPageTypeController/queryDsFormPageTypeList")
	@ResponseBody
	public void queryDsFormPageTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageTypeService.queryDsFormPageTypeList(inputObject, outputObject);
	}
	
	/**
	 * 
	 * @Title: insertDsFormPageType
	 * @Description: 新增动态表单页面分类
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/DsFormPageTypeController/insertDsFormPageType")
	@ResponseBody
	public void insertDsFormPageType(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageTypeService.insertDsFormPageType(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: delDsFormPageTypeById
	 * @Description: 删除动态表单分类
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/DsFormPageTypeController/delDsFormPageTypeById")
	@ResponseBody
	public void delDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageTypeService.delDsFormPageTypeById(inputObject, outputObject);
	}

	/**
	 * 
	 * @Title: queryDsFormPageTypeById
	 * @Description: 根据id查询动态表单页面分类详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/DsFormPageTypeController/queryDsFormPageTypeById")
	@ResponseBody
	public void queryDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageTypeService.queryDsFormPageTypeById(inputObject, outputObject);
	}

	/**
	 * 
	 * @Title: updateDsFormPageTypeById
	 * @Description: 通过id编辑对应的动态表单分类信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/DsFormPageTypeController/updateDsFormPageTypeById")
	@ResponseBody
	public void updateDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageTypeService.updateDsFormPageTypeById(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryDsFormPageTypeByParentId
	 * @Description: 通过parentId查找对应的动态表单分类列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    异常
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/DsFormPageTypeController/queryDsFormPageTypeByParentId")
	@ResponseBody
	public void queryDsFormPageTypeByParentId(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageTypeService.queryDsFormPageTypeByParentId(inputObject, outputObject);
	}
}
