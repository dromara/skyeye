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
	     * @Title: insertSysUserMationById
	     * @Description: 创建账号
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/insertSysUserMationById")
	@ResponseBody
	public void insertSysUserMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.insertSysUserMationById(inputObject, outputObject);
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
	
	/**
	 * 
	     * @Title: queryDeskTopMenuBySession
	     * @Description: 获取桌面菜单列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/queryDeskTopMenuBySession")
	@ResponseBody
	public void queryDeskTopMenuBySession(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.queryDeskTopMenuBySession(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllMenuBySession
	     * @Description: 获取全部菜单列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/queryAllMenuBySession")
	@ResponseBody
	public void queryAllMenuBySession(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.queryAllMenuBySession(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserInstallThemeColor
	     * @Description: 自定义设置主题颜色
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editUserInstallThemeColor")
	@ResponseBody
	public void editUserInstallThemeColor(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editUserInstallThemeColor(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserInstallWinBgPic
	     * @Description: 自定义设置win背景图片
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editUserInstallWinBgPic")
	@ResponseBody
	public void editUserInstallWinBgPic(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editUserInstallWinBgPic(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserInstallWinLockBgPic
	     * @Description: 自定义设置win锁屏背景图片
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editUserInstallWinLockBgPic")
	@ResponseBody
	public void editUserInstallWinLockBgPic(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editUserInstallWinLockBgPic(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserInstallWinStartMenuSize
	     * @Description: 自定义设置win开始菜单尺寸
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editUserInstallWinStartMenuSize")
	@ResponseBody
	public void editUserInstallWinStartMenuSize(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editUserInstallWinStartMenuSize(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserInstallWinTaskPosition
	     * @Description: 自定义设置win任务栏在屏幕的位置
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editUserInstallWinTaskPosition")
	@ResponseBody
	public void editUserInstallWinTaskPosition(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editUserInstallWinTaskPosition(inputObject, outputObject);
	}
	
}
