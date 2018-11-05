package com.skyeye.exexplain.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ExExplainToDsFormContentService {

	public void insertExExplainToDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExExplainToDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editExExplainToDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExExplainToDsFormContentMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
