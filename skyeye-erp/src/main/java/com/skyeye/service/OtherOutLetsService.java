/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface OtherOutLetsService {

	public void queryOtherOutLetsToList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertOtherOutLetsMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryOtherOutLetsToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editOtherOutLetsMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception;

}
