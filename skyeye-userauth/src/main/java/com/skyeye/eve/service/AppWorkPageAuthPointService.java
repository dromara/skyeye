/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface AppWorkPageAuthPointService {

	public void queryAppWorkPageAuthPointListByMenuId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertAppWorkPageAuthPointMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAppWorkPageAuthPointMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editAppWorkPageAuthPointMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteAppWorkPageAuthPointMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
