/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface OilingService {

	public void selectAllOilingMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertOilingMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteOilingById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryOilingMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editOilingMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void selectOilingDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
