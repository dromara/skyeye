/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEvePicTypeService {

	public void querySysPicTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysPicTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateUpSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateDownSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectSysPicTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysPicTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysPicTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysPicTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysPicTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
