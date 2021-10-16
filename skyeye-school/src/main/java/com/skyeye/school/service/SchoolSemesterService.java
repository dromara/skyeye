/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SchoolSemesterService {

	public void querySchoolSemesterList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSchoolSemesterMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolSemesterToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSchoolSemesterById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSchoolSemesterById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolSemesterListToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
