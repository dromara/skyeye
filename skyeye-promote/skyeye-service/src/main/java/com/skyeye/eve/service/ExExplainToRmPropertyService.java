/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ExExplainToRmPropertyService {

	public void insertExExplainToRmPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExExplainToRmPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editExExplainToRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExExplainToRmPropertyMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
