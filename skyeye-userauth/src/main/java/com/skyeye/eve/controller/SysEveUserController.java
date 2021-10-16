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
import com.skyeye.eve.service.SysEveUserService;


@Controller
public class SysEveUserController {
	
	@Autowired
	public SysEveUserService sysEveUserService;
	
	/**
	 * 
	     * @Title: querySysUserList
	     * @Description: 获取管理员用户列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @Title: insertSysUserMationById
	     * @Description: 创建账号
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @Title: editSysUserPasswordMationById
	     * @Description: 重置密码
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editSysUserPasswordMationById")
	@ResponseBody
	public void editSysUserPasswordMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editSysUserPasswordMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUserToLogin
	     * @Description: 登录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
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
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editUserInstallWinTaskPosition")
	@ResponseBody
	public void editUserInstallWinTaskPosition(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editUserInstallWinTaskPosition(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserPassword
	     * @Description: 修改密码
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editUserPassword")
	@ResponseBody
	public void editUserPassword(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editUserPassword(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserInstallVagueBgSrc
	     * @Description: 自定义设置win雾化
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editUserInstallVagueBgSrc")
	@ResponseBody
	public void editUserInstallVagueBgSrc(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editUserInstallVagueBgSrc(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserInstallLoadMenuIconById
	     * @Description: 自定义设置窗口下面展示的是图标还是图标+文字
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editUserInstallLoadMenuIconById")
	@ResponseBody
	public void editUserInstallLoadMenuIconById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editUserInstallLoadMenuIconById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUserLockByLockPwd
	     * @Description: 锁屏密码解锁
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/queryUserLockByLockPwd")
	@ResponseBody
	public void queryUserLockByLockPwd(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.queryUserLockByLockPwd(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUserDetailsMationByUserId
	     * @Description: 修改个人信息时获取数据回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/queryUserDetailsMationByUserId")
	@ResponseBody
	public void queryUserDetailsMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.queryUserDetailsMationByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editUserDetailsMationByUserId
	     * @Description: 修改个人信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/editUserDetailsMationByUserId")
	@ResponseBody
	public void editUserDetailsMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.editUserDetailsMationByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysUserListByUserName
	     * @Description: 获取还没有分配账号的员工
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserController/querySysUserListByUserName")
	@ResponseBody
	public void querySysUserListByUserName(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserService.querySysUserListByUserName(inputObject, outputObject);
	}
	
	/**
     * 
         * @Title: querySysDeskTopByUserId
         * @Description: 获取该用户拥有的桌面
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/SysEveUserController/querySysDeskTopByUserId")
    @ResponseBody
    public void querySysDeskTopByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        sysEveUserService.querySysDeskTopByUserId(inputObject, outputObject);
    }
	
}
