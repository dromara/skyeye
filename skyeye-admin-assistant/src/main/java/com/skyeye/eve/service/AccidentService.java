/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface AccidentService {

	public void selectAllAccidentMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertAccidentMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteAccidentById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAccidentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editAccidentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectAccidentDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
