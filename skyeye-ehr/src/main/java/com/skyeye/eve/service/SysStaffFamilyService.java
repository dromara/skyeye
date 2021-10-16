package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysStaffFamilyService {

	public void queryAllSysStaffFamilyList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysStaffFamilyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysStaffFamilyMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysStaffFamilyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysStaffFamilyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPointStaffSysStaffFamilyList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
