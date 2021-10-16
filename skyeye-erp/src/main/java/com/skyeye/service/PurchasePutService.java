/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface PurchasePutService {

	public void queryPurchasePutToList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertPurchasePutMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPurchasePutMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editPurchasePutMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception;

}
