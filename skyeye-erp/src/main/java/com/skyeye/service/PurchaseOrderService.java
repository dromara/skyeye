/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface PurchaseOrderService {

	public void queryPurchaseOrderToList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertPurchaseOrderMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPurchaseOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editPurchaseOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPurchaseOrderToTurnPutById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertPurchaseOrderToTurnPut(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception;

}
