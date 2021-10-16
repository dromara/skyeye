/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.Map;

public interface SysEveUserStaffService {

	public void querySysUserStaffList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysUserStaffMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysUserStaffById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysUserStaffById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysUserStaffByIdToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysUserStaffState(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editTurnTeacher(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertNewUserMation(Map<String, Object> map) throws Exception;

	public void querySysUserStaffListToTable(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysUserStaffListByIds(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysUserStaffLogin(InputObject inputObject, OutputObject outputObject) throws Exception;
}
