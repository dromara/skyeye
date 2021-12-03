/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface ApiPropertyService {

	void queryApiPropertyMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertApiPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertApiPropertyMationList(List<Map<String, Object>> beans, String userId) throws Exception;

	void deleteApiPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void selectApiPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void editApiPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
