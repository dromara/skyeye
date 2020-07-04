/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CodeModelService {

	public void queryCodeModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCodeModelMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCodeModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCodeModelMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCodeModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
