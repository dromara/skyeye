/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface DsFormContentService {

	public void queryDsFormContentList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDsFormContentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDsFormContentMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryDsFormContentDetailedMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
