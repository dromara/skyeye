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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveRoleController/deleteSysRoleMationById")
	@ResponseBody
	public void deleteSysRoleMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveRoleService.deleteSysRoleMationById(inputObject, outputObject);
	}
	
}
