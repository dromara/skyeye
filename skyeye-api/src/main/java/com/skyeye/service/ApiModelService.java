/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface ApiModelService {

	void queryApiModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertApiModelMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	void insertApiModelList(List<Map<String, Object>> beans, String userId) throws Exception;

	void deleteApiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void selectApiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	void editApiModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
