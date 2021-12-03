/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface ApiGroupService {

	void queryApiGroupMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertApiGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertApiGroupMationList(List<Map<String, Object>> beans, String userId) throws Exception;

	void deleteApiGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void selectApiGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void editApiGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
