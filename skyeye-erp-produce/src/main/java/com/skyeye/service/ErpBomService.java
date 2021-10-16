/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ErpBomService {

	public void queryErpBomList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertErpBomMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpBomDetailById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteErpBomMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpBomMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editErpBomMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpBomListByNormsId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpBomChildProListByBomId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
