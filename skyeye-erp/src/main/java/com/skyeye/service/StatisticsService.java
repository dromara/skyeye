/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface StatisticsService {

	public void queryWarehousingDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryOutgoingDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryInComimgDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySalesDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCustomerReconciliationDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySupplierReconciliationDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryInComimgAllDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySalesAllDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

}
