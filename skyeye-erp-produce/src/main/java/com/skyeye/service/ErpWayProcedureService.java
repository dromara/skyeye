/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ErpWayProcedureService {

	public void queryErpWayProcedureList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertErpWayProcedureMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpWayProcedureMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void downErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void upErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteErpWayProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpWayProcedureDetailsMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpWayRuningProcedureList(InputObject inputObject, OutputObject outputObject) throws Exception;
}
