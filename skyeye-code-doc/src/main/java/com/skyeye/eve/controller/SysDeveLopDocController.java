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
import com.skyeye.eve.service.SysDeveLopDocService;

@Controller
public class SysDeveLopDocController {
	
	@Autowired
	private SysDeveLopDocService sysDeveLopDocService;
	
	/**
	 * 
	     * @Title: querySysDeveLopTypeList
	     * @Description: 获取开发文档目录信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/querySysDeveLopTypeList")
	@ResponseBody
	public void querySysDeveLopTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.querySysDeveLopTypeList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysDeveLopType
	     * @Description: 新增开发文档目录信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/insertSysDeveLopType")
	@ResponseBody
	public void insertSysDeveLopType(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.insertSysDeveLopType(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysDeveLopTypeByIdToEdit
	     * @Description: 编辑开发文档目录信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/querySysDeveLopTypeByIdToEdit")
	@ResponseBody
	public void querySysDeveLopTypeByIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.querySysDeveLopTypeByIdToEdit(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDeveLopTypeById
	     * @Description: 编辑开发文档目录信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/editSysDeveLopTypeById")
	@ResponseBody
	public void editSysDeveLopTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.editSysDeveLopTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysDeveLopTypeById
	     * @Description: 删除开发文档目录信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/deleteSysDeveLopTypeById")
	@ResponseBody
	public void deleteSysDeveLopTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.deleteSysDeveLopTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysDeveLopTypeByFirstType
	     * @Description: 获取一级文档目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/querySysDeveLopTypeByFirstType")
	@ResponseBody
	public void querySysDeveLopTypeByFirstType(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.querySysDeveLopTypeByFirstType(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDeveLopTypeStateISupById
	     * @Description: 开发文档目录上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/editSysDeveLopTypeStateISupById")
	@ResponseBody
	public void editSysDeveLopTypeStateISupById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.editSysDeveLopTypeStateISupById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDeveLopTypeStateISdownById
	     * @Description: 开发文档目录下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/editSysDeveLopTypeStateISdownById")
	@ResponseBody
	public void editSysDeveLopTypeStateISdownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.editSysDeveLopTypeStateISdownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDeveLopTypeOrderByISupById
	     * @Description: 开发文档目录上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/editSysDeveLopTypeOrderByISupById")
	@ResponseBody
	public void editSysDeveLopTypeOrderByISupById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.editSysDeveLopTypeOrderByISupById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDeveLopTypeOrderByISdownById
	     * @Description: 开发文档目录下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/editSysDeveLopTypeOrderByISdownById")
	@ResponseBody
	public void editSysDeveLopTypeOrderByISdownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.editSysDeveLopTypeOrderByISdownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysDeveLopDocList
	     * @Description: 获取开发文档信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/querySysDeveLopDocList")
	@ResponseBody
	public void querySysDeveLopDocList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.querySysDeveLopDocList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: addSysDeveLopDoc
	     * @Description: 新增开发文档信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/addSysDeveLopDoc")
	@ResponseBody
	public void addSysDeveLopDoc(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.addSysDeveLopDoc(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysDeveLopDocByIdToEdit
	     * @Description: 编辑开发文档信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/querySysDeveLopDocByIdToEdit")
	@ResponseBody
	public void querySysDeveLopDocByIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.querySysDeveLopDocByIdToEdit(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDeveLopDocById
	     * @Description: 编辑开发文档信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/editSysDeveLopDocById")
	@ResponseBody
	public void editSysDeveLopDocById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.editSysDeveLopDocById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysDeveLopDocById
	     * @Description: 删除开发文档信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/deleteSysDeveLopDocById")
	@ResponseBody
	public void deleteSysDeveLopDocById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.deleteSysDeveLopDocById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDeveLopDocStateISupById
	     * @Description: 开发文档上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/editSysDeveLopDocStateISupById")
	@ResponseBody
	public void editSysDeveLopDocStateISupById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.editSysDeveLopDocStateISupById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDeveLopDocStateISdownById
	     * @Description: 开发文档下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/editSysDeveLopDocStateISdownById")
	@ResponseBody
	public void editSysDeveLopDocStateISdownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.editSysDeveLopDocStateISdownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDeveLopDocOrderByISupById
	     * @Description: 开发文档上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/editSysDeveLopDocOrderByISupById")
	@ResponseBody
	public void editSysDeveLopDocOrderByISupById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.editSysDeveLopDocOrderByISupById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysDeveLopDocOrderByISdownById
	     * @Description: 开发文档下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/editSysDeveLopDocOrderByISdownById")
	@ResponseBody
	public void editSysDeveLopDocOrderByISdownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.editSysDeveLopDocOrderByISdownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysDeveLopFirstTypeToShow
	     * @Description: 获取一级分类列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/querySysDeveLopFirstTypeToShow")
	@ResponseBody
	public void querySysDeveLopFirstTypeToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.querySysDeveLopFirstTypeToShow(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysDeveLopSecondTypeToShow
	     * @Description: 获取二级分类列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/querySysDeveLopSecondTypeToShow")
	@ResponseBody
	public void querySysDeveLopSecondTypeToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.querySysDeveLopSecondTypeToShow(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysDeveLopDocToShow
	     * @Description: 获取文档标题
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/querySysDeveLopDocToShow")
	@ResponseBody
	public void querySysDeveLopDocToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.querySysDeveLopDocToShow(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysDeveLopDocContentToShow
	     * @Description: 获取文档内容
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDeveLopDocController/querySysDeveLopDocContentToShow")
	@ResponseBody
	public void querySysDeveLopDocContentToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDeveLopDocService.querySysDeveLopDocContentToShow(inputObject, outputObject);
	}
	
}
