/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SchoolBodyMindService {

	public void querySchoolBodyMindList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSchoolBodyMindMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolBodyMindToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSchoolBodyMindById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSchoolBodyMindById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySchoolBodyMindListToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
