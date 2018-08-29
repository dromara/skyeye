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
	
	
	
}
