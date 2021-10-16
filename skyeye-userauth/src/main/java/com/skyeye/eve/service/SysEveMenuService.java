/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveMenuService {

	public void querySysMenuList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysMenuMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysMenuMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysMenuMationBySimpleLevel(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysMenuMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysMenuMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryTreeSysMenuMationBySimpleLevel(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysMenuLevelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysEveMenuSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysEveMenuSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void querySysWinMationListBySysId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void querySysEveMenuBySysId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
