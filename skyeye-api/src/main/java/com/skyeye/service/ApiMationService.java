/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface ApiMationService {

	void queryApiMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertApiMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertApiMationList(List<Map<String, Object>> beans, String userId) throws Exception;

	void deleteApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void selectApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void editApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
