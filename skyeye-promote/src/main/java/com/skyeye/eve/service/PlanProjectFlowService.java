package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface PlanProjectFlowService {

	public void queryPlanProjectFlowList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertPlanProjectFlowMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deletePlanProjectFlowMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPlanProjectFlowMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editPlanProjectFlowMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
