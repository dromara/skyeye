/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SchoolGradeMationService {

	public void querySchoolGradeMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSchoolGradeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSchoolGradeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolGradeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSchoolGradeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllGradeMationBySchoolId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editGradeMationOrderNumToUp(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editGradeMationOrderNumToDown(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolGradeNowYearMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
