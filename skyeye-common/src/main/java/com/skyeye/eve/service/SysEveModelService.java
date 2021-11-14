/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveModelService {

	public void querySysEveModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysEveModelMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysEveModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectSysEveModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectSysEveModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysEveModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	}
