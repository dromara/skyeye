package com.skyeye.authority.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.authority.service.SysEveUserService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;


@Controller
public class SysEveUserController {
	
	@Autowired
	public SysEveUserService sysEveUserService;
	
	/**
	 * 
	     * @Title: querySysUserList
	     * @Description: 获取管理员用户列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/querySysUserList")
	@ResponseBody
	public void querySysUserList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.querySysUserList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysUserLockStateToLockById
	     * @Description: 锁定账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editSysUserLockStateToLockById")
	@ResponseBody
	public void editSysUserLockStateToLockById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editSysUserLockStateToLockById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysUserLockStateToUnLockById
	     * @Description: 解锁账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editSysUserLockStateToUnLockById")
	@ResponseBody
	public void editSysUserLockStateToUnLockById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editSysUserLockStateToUnLockById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysUserMationToEditById
	     * @Description: 编辑账号时获取账号信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/querySysUserMationToEditById")
	@ResponseBody
	public void querySysUserMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.querySysUserMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysUserMationById
	     * @Description: 编辑账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editSysUserMationById")
	@ResponseBody
	public void editSysUserMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editSysUserMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUserToLogin
	     * @Description: 登录
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/queryUserToLogin")
	@ResponseBody
	public void queryUserToLogin(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.queryUserToLogin(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUserMationBySession
	     * @Description: 从session中获取用户信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/queryUserMationBySession")
	@ResponseBody
	public void queryUserMationBySession(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.queryUserMationBySession(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteUserMationBySession
	     * @Description: 退出
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/deleteUserMationBySession")
	@ResponseBody
	public void deleteUserMationBySession(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.deleteUserMationBySession(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryRoleAndBindRoleByUserId
	     * @Description: 获取角色和当前已经绑定的角色信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/queryRoleAndBindRoleByUserId")
	@ResponseBody
	public void queryRoleAndBindRoleByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.queryRoleAndBindRoleByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRoleIdsByUserId
	     * @Description: 编辑用户绑定的角色
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editRoleIdsByUserId")
	@ResponseBody
	public void editRoleIdsByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editRoleIdsByUserId(inputObject, outputObject);
	}
	
}
