/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ErpPickService {

	public void queryRequisitionMaterialOrderList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryReturnMaterialOrderList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPatchMaterialOrderList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertRequisitionMaterialOrder(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRequisitionMaterialOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editRequisitionMaterialOrderById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPickOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deletePickOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertPatchMaterialOrder(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPatchMaterialOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editPatchMaterialOrderById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertReturnMaterialOrder(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryReturnMaterialOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editReturnMaterialOrderById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editPickOrderMationToSubById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editPickOrderMationToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateDepartMentStock(String departMentId, String materialId, String normsId, String operNumber, int type) throws Exception;

}
