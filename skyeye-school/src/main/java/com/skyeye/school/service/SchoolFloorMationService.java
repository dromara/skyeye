/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SchoolFloorMationService {

	public void querySchoolFloorMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSchoolFloorMationMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolFloorMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSchoolFloorMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSchoolFloorMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolFloorMationToSelectList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
