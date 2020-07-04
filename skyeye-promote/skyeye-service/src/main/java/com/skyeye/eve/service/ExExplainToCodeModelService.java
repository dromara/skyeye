/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ExExplainToCodeModelService {

	public void insertExExplainToCodeModelMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExExplainToCodeModelMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editExExplainToCodeModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExExplainToCodeModelMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
