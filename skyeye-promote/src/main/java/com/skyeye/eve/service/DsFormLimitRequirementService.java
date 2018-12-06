package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface DsFormLimitRequirementService {

	public void queryDsFormLimitRequirementList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDsFormLimitRequirementMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteDsFormLimitRequirementMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDsFormLimitRequirementMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDsFormLimitRequirementMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDsFormLimitRequirementMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
