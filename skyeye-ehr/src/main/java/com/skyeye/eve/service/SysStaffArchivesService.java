package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysStaffArchivesService {

	public void queryAllSysStaffArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysLeaveStaffArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysStaffNotInArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysStaffNoArchivesList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysStaffArchivesMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysStaffArchivesMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysStaffArchivesMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
