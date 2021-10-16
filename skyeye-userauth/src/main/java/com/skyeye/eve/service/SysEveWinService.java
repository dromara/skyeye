/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveWinService {

	public void queryWinMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertWinMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryWinMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editWinMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteWinMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editAuthorizationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCancleAuthorizationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryWinMationListToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertWinMationImportantSynchronization(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryWinMationImportantSynchronizationData(InputObject inputObject, OutputObject outputObject) throws Exception;

}
