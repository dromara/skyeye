/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveMenuAuthPointService {

	public void querySysEveMenuAuthPointListByMenuId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysEveMenuAuthPointMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysEveMenuAuthPointMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysEveMenuAuthPointMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysEveMenuAuthPointMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
