package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CompanyDepartmentService {

	public void queryCompanyDepartmentList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCompanyDepartmentMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCompanyDepartmentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyDepartmentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCompanyDepartmentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyDepartmentListTreeByCompanyId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
