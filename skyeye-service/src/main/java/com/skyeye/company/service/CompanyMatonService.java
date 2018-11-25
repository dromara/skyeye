package com.skyeye.company.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CompanyMatonService {

	public void queryCompanyMatonList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCompanyMatonMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCompanyMatonMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyMatonMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCompanyMatonMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
