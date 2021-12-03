/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.Map;

public interface SysEveUserService {

	public void querySysUserList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysUserLockStateToLockById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysUserLockStateToUnLockById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysUserPasswordMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUserToLogin(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUserMationBySession(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteUserMationBySession(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRoleAndBindRoleByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRoleIdsByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDeskTopMenuBySession(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllMenuBySession(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editUserInstallThemeColor(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editUserInstallWinBgPic(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editUserInstallWinLockBgPic(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editUserInstallWinStartMenuSize(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editUserInstallWinTaskPosition(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysUserMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editUserPassword(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editUserInstallVagueBgSrc(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editUserInstallLoadMenuIconById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUserLockByLockPwd(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryUserDetailsMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editUserDetailsMationByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysUserListByUserName(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDeskTopByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 根据用户id获取用户信息
	 *
	 * @param userId 用户id
	 * @return 用户信息
	 */
	Map<String, Object> getUserMationByUserId(String userId);
	
}
