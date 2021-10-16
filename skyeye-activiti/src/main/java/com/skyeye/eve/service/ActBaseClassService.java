/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ActBaseClassService {

	public void queryActBaseClassList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertActBaseClassMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteActBaseClassById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectActBaseClassById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActBaseClassMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectActBaseClassByIdToDedails(InputObject inputObject, OutputObject outputObject) throws Exception;

}
