package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysStaffContractService {

	public void queryAllSysStaffContractList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysStaffContractMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysStaffContractMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysStaffContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysStaffContractMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPointStaffSysStaffContractList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void signSysStaffContractById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
