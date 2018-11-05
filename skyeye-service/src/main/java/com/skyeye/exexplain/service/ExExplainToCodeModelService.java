package com.skyeye.exexplain.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ExExplainToCodeModelService {

	public void insertExExplainToCodeModelMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExExplainToCodeModelMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editExExplainToCodeModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryExExplainToCodeModelMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
