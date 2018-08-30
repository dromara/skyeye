package com.skyeye.authority.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.authority.service.SysEveRoleService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

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
	
}
