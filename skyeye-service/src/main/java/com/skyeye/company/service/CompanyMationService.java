package com.skyeye.company.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CompanyMationService {

	public void queryCompanyMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCompanyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCompanyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCompanyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
