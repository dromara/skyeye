/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface PlanProjectService {

	public void queryPlanProjectList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertPlanProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deletePlanProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPlanProjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editPlanProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
