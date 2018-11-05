package com.skyeye.codemodel.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CodeModelHistoryService {

	public void queryCodeModelHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCodeModelHistoryCreate(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void downloadCodeModelHistory(InputObject inputObject, OutputObject outputObject) throws Exception;

}
