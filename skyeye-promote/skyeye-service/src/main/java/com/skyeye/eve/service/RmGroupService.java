/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface RmGroupService {

	public void queryRmGroupList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertRmGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteRmGroupById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmGroupMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmGroupSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmGroupAllList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
