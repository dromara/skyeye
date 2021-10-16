/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface RmTypeService {

	public void queryRmTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertRmTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteRmTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRmTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRmTypeAllList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
