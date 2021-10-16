/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ErpProductionService {

	public void queryErpProductionList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertErpProductionMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpProductionMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editErpProductionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteErpProductionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpProductionMationToDetailById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void submitApplicationForApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editErpProductionStateToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpProductionListToTable(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpProductionOutsideProListByOrderId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
