/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SmProjectPageService {

	public void queryProPageMationByProIdList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertProPageMationByProId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSmProjectPageSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSmProjectPageSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySmProjectPageMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSmProjectPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSmProjectPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
