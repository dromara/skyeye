/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface PlanProjectFlowService {

	public void queryPlanProjectFlowList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertPlanProjectFlowMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deletePlanProjectFlowMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPlanProjectFlowMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editPlanProjectFlowMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPlanProjectFlowJsonContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editPlanProjectFlowJsonContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
