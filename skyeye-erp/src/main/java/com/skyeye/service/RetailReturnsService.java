/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface RetailReturnsService {

	public void queryRetailReturnsToList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertRetailReturnsMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRetailReturnsMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRetailReturnsMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception;

}
