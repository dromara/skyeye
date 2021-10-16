/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CompanyMationService {

	public void queryCompanyMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCompanyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCompanyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCompanyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryOverAllCompanyMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyMationListTree(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyOrganization(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyMationListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCompanyMationListByIds(InputObject inputObject, OutputObject outputObject) throws Exception;
}
