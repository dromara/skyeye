/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SalesOrderService {

	public void querySalesOrderToList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSalesOrderMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySalesOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSalesOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySalesOrderToTurnPutById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSalesOrderToTurnPut(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySalesOrderListToTree(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySalesOrderMaterialListByOrderId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
