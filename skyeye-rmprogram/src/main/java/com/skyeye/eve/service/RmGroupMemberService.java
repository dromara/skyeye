/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface RmGroupMemberService {

	public void queryRmGroupMemberList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertRmGroupMemberMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupMemberSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupMemberSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteRmGroupMemberById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmGroupMemberMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupMemberMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupMemberAndPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmGroupMemberAndPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
