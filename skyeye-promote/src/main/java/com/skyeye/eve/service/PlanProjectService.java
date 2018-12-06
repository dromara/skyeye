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
