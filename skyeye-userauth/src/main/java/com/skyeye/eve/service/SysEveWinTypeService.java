/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveWinTypeService {

	public void querySysWinTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysWinFirstTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysWinTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysWinTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysWinTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysWinFirstTypeListNotIsThisId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysWinTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysWinTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysWinTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysWinTypeMationStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysWinTypeMationStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysWinTypeFirstMationStateIsUp(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysWinTypeSecondMationStateIsUp(InputObject inputObject, OutputObject outputObject) throws Exception;

}
