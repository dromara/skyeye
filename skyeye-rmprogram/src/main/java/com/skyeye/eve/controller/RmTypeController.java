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
import com.skyeye.eve.service.RmTypeService;

@Controller
public class RmTypeController {
	
	@Autowired
	private RmTypeService rmTypeService;
	
	/**
	 * 
	     * @Title: queryRmTypeList
	     * @Description: 获取小程序分类列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/queryRmTypeList")
	@ResponseBody
	public void queryRmTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.queryRmTypeList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertRmTypeMation
	     * @Description: 新增小程序分类列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/insertRmTypeMation")
	@ResponseBody
	public void insertRmTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.insertRmTypeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteRmTypeById
	     * @Description: 删除小程序分类信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/deleteRmTypeById")
	@ResponseBody
	public void deleteRmTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.deleteRmTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryRmTypeMationToEditById
	     * @Description: 编辑小程序分类信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/queryRmTypeMationToEditById")
	@ResponseBody
	public void queryRmTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.queryRmTypeMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmTypeMationById
	     * @Description: 编辑小程序分类信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/editRmTypeMationById")
	@ResponseBody
	public void editRmTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.editRmTypeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmTypeSortTopById
	     * @Description: 小程序分类展示顺序上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/editRmTypeSortTopById")
	@ResponseBody
	public void editRmTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.editRmTypeSortTopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmTypeSortLowerById
	     * @Description: 小程序分类展示顺序下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/editRmTypeSortLowerById")
	@ResponseBody
	public void editRmTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.editRmTypeSortLowerById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryRmTypeAllList
	     * @Description: 获取所有小程序分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/queryRmTypeAllList")
	@ResponseBody
	public void queryRmTypeAllList(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.queryRmTypeAllList(inputObject, outputObject);
	}
	
}
