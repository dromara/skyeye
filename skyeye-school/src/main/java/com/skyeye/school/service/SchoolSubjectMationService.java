/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SchoolSubjectMationService {

	public void querySchoolSubjectMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSchoolSubjectMationMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolSubjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSchoolSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSchoolSubjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolSubjectMationListToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolSubjectMationListToShowByGradeId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
