package com.skyeye.codemodel.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CodeModelGroupService {

	public void queryCodeModelGroupList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCodeModelGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCodeModelGroupById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCodeModelGroupMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCodeModelGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryTableParameterByTableName(InputObject inputObject, OutputObject outputObject) throws Exception;

}
