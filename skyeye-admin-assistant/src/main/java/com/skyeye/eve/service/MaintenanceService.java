/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface MaintenanceService {

	public void selectAllMaintenanceMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertMaintenanceMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteMaintenanceById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMaintenanceMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMaintenanceMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectMaintenanceDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
