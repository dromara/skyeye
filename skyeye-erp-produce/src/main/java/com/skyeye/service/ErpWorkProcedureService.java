/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ErpWorkProcedureService {

	public void queryErpWorkProcedureList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertErpWorkProcedureMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpWorkProcedureToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deletErpWorkProcedureById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editErpWorkProcedureById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpWorkProcedureListToTable(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpWorkProcedureListByIds(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpWorkProcedureMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpWorkProcedureListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;

}
