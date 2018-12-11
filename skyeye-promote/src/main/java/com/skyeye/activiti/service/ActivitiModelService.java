package com.skyeye.activiti.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ActivitiModelService {

	public void insertNewActivitiModel(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToDeploy(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToStartProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryReleasedActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteReleasedActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
