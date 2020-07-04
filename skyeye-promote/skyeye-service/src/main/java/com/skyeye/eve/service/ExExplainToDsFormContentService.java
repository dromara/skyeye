/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ExExplainToDsFormContentService {

	public void insertExExplainToDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExExplainToDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editExExplainToDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExExplainToDsFormContentMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
