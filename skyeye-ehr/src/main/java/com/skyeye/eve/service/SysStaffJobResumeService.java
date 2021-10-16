package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysStaffJobResumeService {

	public void queryAllSysStaffJobResumeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysStaffJobResumeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysStaffJobResumeMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysStaffJobResumeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysStaffJobResumeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPointStaffSysStaffJobResumeList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
