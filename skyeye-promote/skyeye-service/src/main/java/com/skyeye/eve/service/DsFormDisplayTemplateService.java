package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface DsFormDisplayTemplateService {

	public void queryDsFormDisplayTemplateList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDsFormDisplayTemplateMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDsFormDisplayTemplateMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDsFormDisplayTemplateMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDisplayTemplateListToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
