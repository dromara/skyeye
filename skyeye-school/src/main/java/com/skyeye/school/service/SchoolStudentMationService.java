/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SchoolStudentMationService {

	public void querySchoolStudentMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSchoolStudentMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryNotDividedIntoClassesSchoolStudentMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolStudentMationToOperatorById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editAssignmentClassByStuId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolStudentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSchoolStudentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolStudentMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void exportSchoolStudentMationModel(InputObject inputObject, OutputObject outputObject) throws Exception;

}
