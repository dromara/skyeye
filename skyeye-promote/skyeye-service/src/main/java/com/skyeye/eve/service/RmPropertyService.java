/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface RmPropertyService {

	public void queryRmPropertyList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertRmPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmPropertyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmPropertyListToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
