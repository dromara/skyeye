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
import com.skyeye.eve.service.SysEveWinDragDropService;

@Controller
public class SysEveWinDragDropController {
	
	@Autowired
	private SysEveWinDragDropService sysEveWinDragDropService;
	
	/**
	 * 
	     * @Title: insertWinCustomMenuBox
	     * @Description: 用户自定义创建菜单盒子
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinDragDropController/insertWinCustomMenuBox")
	@ResponseBody
	public void insertWinCustomMenuBox(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinDragDropService.insertWinCustomMenuBox(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertWinCustomMenu
	     * @Description: 用户自定义创建菜单
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinDragDropController/insertWinCustomMenu")
	@ResponseBody
	public void insertWinCustomMenu(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinDragDropService.insertWinCustomMenu(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteWinMenuOrBoxById
	     * @Description: 用户删除自定义菜单或文件夹
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinDragDropController/deleteWinMenuOrBoxById")
	@ResponseBody
	public void deleteWinMenuOrBoxById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinDragDropService.deleteWinMenuOrBoxById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editMenuParentIdById
	     * @Description: 用户自定义父菜单
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinDragDropController/editMenuParentIdById")
	@ResponseBody
	public void editMenuParentIdById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinDragDropService.editMenuParentIdById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMenuMationTypeById
	     * @Description: 获取菜单类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinDragDropController/queryMenuMationTypeById")
	@ResponseBody
	public void queryMenuMationTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinDragDropService.queryMenuMationTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCustomMenuBoxMationEditById
	     * @Description: 编辑自定义盒子时回显信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinDragDropController/queryCustomMenuBoxMationEditById")
	@ResponseBody
	public void queryCustomMenuBoxMationEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinDragDropService.queryCustomMenuBoxMationEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCustomMenuBoxMationById
	     * @Description: 编辑自定义盒子
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinDragDropController/editCustomMenuBoxMationById")
	@ResponseBody
	public void editCustomMenuBoxMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinDragDropService.editCustomMenuBoxMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCustomMenuMationEditById
	     * @Description: 编辑快捷方式时回显信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinDragDropController/queryCustomMenuMationEditById")
	@ResponseBody
	public void queryCustomMenuMationEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinDragDropService.queryCustomMenuMationEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCustomMenuMationById
	     * @Description: 编辑快捷方式
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinDragDropController/editCustomMenuMationById")
	@ResponseBody
	public void editCustomMenuMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinDragDropService.editCustomMenuMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCustomMenuToDeskTopById
	     * @Description: 系统菜单发送到桌面快捷方式
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinDragDropController/editCustomMenuToDeskTopById")
	@ResponseBody
	public void editCustomMenuToDeskTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinDragDropService.editCustomMenuToDeskTopById(inputObject, outputObject);
	}
	
}
