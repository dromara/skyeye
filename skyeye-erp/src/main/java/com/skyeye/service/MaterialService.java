/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface MaterialService {

	public void queryMaterialList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertMaterialMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMaterialEnabledToDisablesById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMaterialEnabledToEnablesById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteMaterialMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialMationDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMaterialMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialListToTable(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialTockByNormsId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialDepotItemByNormsId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialListByIds(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialListByIdsToProduce(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialBomChildsToProduceByJson(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialReserveList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaterialInventoryWarningList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
