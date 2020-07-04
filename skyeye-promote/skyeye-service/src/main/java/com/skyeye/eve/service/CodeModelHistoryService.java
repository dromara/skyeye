/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CodeModelHistoryService {

	public void queryCodeModelHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCodeModelHistoryCreate(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void downloadCodeModelHistory(InputObject inputObject, OutputObject outputObject) throws Exception;

}
