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
import com.skyeye.eve.service.SysEveMenuService;

@Controller
public class SysEveMenuController {
	
	@Autowired
	private SysEveMenuService sysEveMenuService;
	
	/**
	 * 
	     * @Title: querySysMenuList
	     * @Description: 获取菜单列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/querySysMenuList")
	@ResponseBody
	public void querySysMenuList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.querySysMenuList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysMenuMation
	     * @Description: 添加菜单
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/insertSysMenuMation")
	@ResponseBody
	public void insertSysMenuMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.insertSysMenuMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysMenuMationBySimpleLevel
	     * @Description: 查看同级菜单
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/querySysMenuMationBySimpleLevel")
	@ResponseBody
	public void querySysMenuMationBySimpleLevel(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.querySysMenuMationBySimpleLevel(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysMenuMationToEditById
	     * @Description: 编辑菜单时进行信息回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/querySysMenuMationToEditById")
	@ResponseBody
	public void querySysMenuMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.querySysMenuMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysMenuMationById
	     * @Description: 编辑菜单信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/editSysMenuMationById")
	@ResponseBody
	public void editSysMenuMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.editSysMenuMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysMenuMationById
	     * @Description: 删除菜单信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/deleteSysMenuMationById")
	@ResponseBody
	public void deleteSysMenuMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.deleteSysMenuMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryTreeSysMenuMationBySimpleLevel
	     * @Description: 异步加载树查看同级菜单
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/queryTreeSysMenuMationBySimpleLevel")
	@ResponseBody
	public void queryTreeSysMenuMationBySimpleLevel(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.queryTreeSysMenuMationBySimpleLevel(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysMenuLevelList
	     * @Description: 获取菜单级别列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/querySysMenuLevelList")
	@ResponseBody
	public void querySysMenuLevelList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.querySysMenuLevelList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysEveMenuSortTopById
	     * @Description: 菜单展示顺序上移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/editSysEveMenuSortTopById")
	@ResponseBody
	public void editSysEveMenuSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.editSysEveMenuSortTopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysEveMenuSortLowerById
	     * @Description: 菜单展示顺序下移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/editSysEveMenuSortLowerById")
	@ResponseBody
	public void editSysEveMenuSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.editSysEveMenuSortLowerById(inputObject, outputObject);
	}
	
}
