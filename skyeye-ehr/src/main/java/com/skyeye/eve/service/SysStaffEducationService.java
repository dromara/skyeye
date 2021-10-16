package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysStaffEducationService {

	public void queryAllSysStaffEducationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysStaffEducationMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysStaffEducationMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysStaffEducationMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysStaffEducationMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPointStaffSysStaffEducationList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
