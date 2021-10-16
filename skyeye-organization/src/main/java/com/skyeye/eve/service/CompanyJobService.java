/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
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

	public void queryCompanyJobListByToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyJobSimpleListByToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;

}
