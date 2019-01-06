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

	public void addQuRadioMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuCheckBoxMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuMultiFillblankMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuParagraphMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addQuChenMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionChenColumnMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionChenRowMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionRadioOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionChedkBoxOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionScoreOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionOrderOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteQuestionMultiFillblankOptionMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSurveyStateToReleaseById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDwSurveyDirectoryMationByIdToHTML(InputObject inputObject, OutputObject outputObject) throws Exception;

}
