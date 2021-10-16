/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SmProjectService {

	public void querySmProjectList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSmProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSmProjectById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySmProjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSmProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryGroupMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryGroupMemberMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
