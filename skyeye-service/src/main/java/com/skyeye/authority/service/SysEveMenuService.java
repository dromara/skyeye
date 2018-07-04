package com.skyeye.authority.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveMenuService {

	public void querySysMenuList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysMenuMation(InputObject inputObject, OutputObject outputObject) throws Exception;

}
