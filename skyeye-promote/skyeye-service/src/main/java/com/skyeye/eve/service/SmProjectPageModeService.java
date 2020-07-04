/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SmProjectPageModeService {

	public void queryProPageModeMationByPageIdList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProPageModeMationByPageIdList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPropertyListByMemberId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
