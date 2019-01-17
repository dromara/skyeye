package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CompanyJobService {

	public void queryCompanyJobList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCompanyJobMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCompanyJobMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyJobMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCompanyJobMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyJobListTreeByDepartmentId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
