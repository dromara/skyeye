package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface DwSurveyDirectoryService {

	public void queryDwSurveyDirectoryList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDwSurveyDirectoryMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDwSurveyDirectoryMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
