/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SchoolMationService {

	public void querySchoolMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSchoolMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSchoolMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSchoolMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryOverAllSchoolMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolPowerListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;

}
