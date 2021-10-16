/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface InspectionService {

	public void selectAllInspectionMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertInspectionMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteInspectionById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryInspectionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editInspectionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectInspectionDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
