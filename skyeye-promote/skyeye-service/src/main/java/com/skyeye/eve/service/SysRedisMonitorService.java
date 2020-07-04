/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysRedisMonitorService {

	public void queryRedisInfoList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRedisLogsList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRedisKeysList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
