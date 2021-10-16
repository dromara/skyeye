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
import com.skyeye.eve.service.SysEveRoleService;

@Controller
public class SysEveRoleController {
	
	@Autowired
	private SysEveRoleService sysEveRoleService;
	
	/**
	 * 
	     * @Title: querySysRoleList
	     * @Description: 获取角色列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveRoleController/querySysRoleList")
	@ResponseBody
	public void querySysRoleList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveRoleService.querySysRoleList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysRoleBandMenuList
	     * @Description: 获取角色需要绑定的菜单列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveRoleController/querySysRoleBandMenuList")
	@ResponseBody
	public void querySysRoleBandMenuList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveRoleService.querySysRoleBandMenuList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysRoleMation
	     * @Description: 新增角色
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveRoleController/insertSysRoleMation")
	@ResponseBody
	public void insertSysRoleMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveRoleService.insertSysRoleMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysRoleMationToEditById
	     * @Description: 编辑角色时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveRoleController/querySysRoleMationToEditById")
	@ResponseBody
	public void querySysRoleMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveRoleService.querySysRoleMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysRoleMationById
	     * @Description: 编辑角色
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveRoleController/editSysRoleMationById")
	@ResponseBody
	public void editSysRoleMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveRoleService.editSysRoleMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysRoleMationById
	     * @Description: 删除角色
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveRoleController/deleteSysRoleMationById")
	@ResponseBody
	public void deleteSysRoleMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveRoleService.deleteSysRoleMationById(inputObject, outputObject);
	}
	
	/**
     * 
         * @Title: querySysRoleBandAppMenuList
         * @Description: 获取角色需要绑定的手机端菜单列表
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/SysEveRoleController/querySysRoleBandAppMenuList")
    @ResponseBody
    public void querySysRoleBandAppMenuList(InputObject inputObject, OutputObject outputObject) throws Exception{
        sysEveRoleService.querySysRoleBandAppMenuList(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: querySysRoleToAppMenuEditById
         * @Description: 手机端菜单授权时的信息回显
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/SysEveRoleController/querySysRoleToAppMenuEditById")
    @ResponseBody
    public void querySysRoleToAppMenuEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sysEveRoleService.querySysRoleToAppMenuEditById(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: editSysRoleAppMenuById
         * @Description: 手机端菜单授权
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/SysEveRoleController/editSysRoleAppMenuById")
    @ResponseBody
    public void editSysRoleAppMenuById(InputObject inputObject, OutputObject outputObject) throws Exception{
        sysEveRoleService.editSysRoleAppMenuById(inputObject, outputObject);
    }
	
}
