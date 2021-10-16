/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface MaterialUnitService {

	public void queryMaterialUnitList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertMaterialUnitMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteMaterialUnitMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialUnitMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMaterialUnitMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialUnitListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;

}
