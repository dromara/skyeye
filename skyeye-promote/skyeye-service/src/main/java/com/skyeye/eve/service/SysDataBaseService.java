/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysDataBaseService {

	public void querySysDataBaseList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDataBaseSelectList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDataBaseDescSelectList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
