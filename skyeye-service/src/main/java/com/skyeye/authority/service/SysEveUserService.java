package com.skyeye.authority.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveUserService {

	public void querySysUserList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysUserLockStateToLockById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysUserLockStateToUnLockById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysUserMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysUserMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

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
	
	
	
}
