/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SchoolFamilySituationService {

	public void querySchoolFamilySituationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSchoolFamilySituationMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolFamilySituationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSchoolFamilySituationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSchoolFamilySituationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolFamilySituationListToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
