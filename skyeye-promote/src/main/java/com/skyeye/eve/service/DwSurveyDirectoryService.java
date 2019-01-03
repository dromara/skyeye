package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface DwSurveyDirectoryService {

	public void queryDwSurveyDirectoryList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDwSurveyDirectoryMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDwSurveyDirectoryMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDwSurveyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDwSurveyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuScoreMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuOrderquMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuPagetagMation(InputObject inputObject, OutputObject outputObject) throws Exception;

}
