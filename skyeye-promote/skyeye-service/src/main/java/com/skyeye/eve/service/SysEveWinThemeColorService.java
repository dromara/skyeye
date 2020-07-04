/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveWinThemeColorService {

	public void querySysEveWinThemeColorList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysEveWinThemeColorMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysEveWinThemeColorMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysEveWinThemeColorMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysEveWinThemeColorMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
