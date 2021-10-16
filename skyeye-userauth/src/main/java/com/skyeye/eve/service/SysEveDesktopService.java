/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveDesktopService {

	public void querySysDesktopList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysDesktopMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateUpSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateDownSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectSysDesktopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDesktopMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDesktopMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDesktopMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllSysDesktopList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void removeAllSysEveMenuByDesktopId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
