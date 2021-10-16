/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface InsuranceService {

	public void selectAllInsuranceMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertInsuranceMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteInsuranceById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryInsuranceMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editInsuranceMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectInsuranceDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
