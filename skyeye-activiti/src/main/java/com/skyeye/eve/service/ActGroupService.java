/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ActGroupService {

	public void insertActGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectAllActGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertActGroupUserByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActGroupNameByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteActGroupByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteActGroupUserByGroupIdAndUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectUserInfoOnActGroup(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteAllActGroupUserByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception;
	

}
