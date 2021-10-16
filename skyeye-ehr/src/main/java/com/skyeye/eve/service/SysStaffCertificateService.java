package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysStaffCertificateService {

	public void queryAllSysStaffCertificateList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysStaffCertificateMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysStaffCertificateMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysStaffCertificateMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysStaffCertificateMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPointStaffSysStaffCertificateList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
