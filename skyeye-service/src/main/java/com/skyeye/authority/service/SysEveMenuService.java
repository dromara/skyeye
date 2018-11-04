package com.skyeye.authority.service;

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

}
