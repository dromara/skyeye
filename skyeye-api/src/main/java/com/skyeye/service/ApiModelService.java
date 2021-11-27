/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ApiModelService {

	public void queryApiModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertApiModelMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteApiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectApiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editApiModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	}
