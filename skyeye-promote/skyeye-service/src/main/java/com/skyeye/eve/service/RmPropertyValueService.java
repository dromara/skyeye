/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface RmPropertyValueService {

	public void queryRmPropertyValueList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertRmPropertyValueMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteRmPropertyValueMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmPropertyValueMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmPropertyValueMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
