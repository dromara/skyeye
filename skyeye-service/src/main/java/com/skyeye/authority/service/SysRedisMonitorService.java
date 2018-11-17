package com.skyeye.authority.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysRedisMonitorService {

	public void queryRedisInfoList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRedisLogsList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryRedisKeysList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
