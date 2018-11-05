package com.skyeye.dsform.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface DsFormContentService {

	public void queryDsFormContentList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDsFormContentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
