package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveRoleService {

	public void querySysRoleList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysRoleBandMenuList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysRoleMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysRoleMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysRoleMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysRoleMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
