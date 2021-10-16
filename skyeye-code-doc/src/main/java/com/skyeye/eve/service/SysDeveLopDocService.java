/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysDeveLopDocService {

	public void querySysDeveLopTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSysDeveLopType(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDeveLopTypeByIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDeveLopTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysDeveLopTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDeveLopTypeByFirstType(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDeveLopTypeStateISupById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDeveLopTypeStateISdownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDeveLopTypeOrderByISupById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDeveLopTypeOrderByISdownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDeveLopDocList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void addSysDeveLopDoc(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDeveLopDocByIdToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDeveLopDocById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysDeveLopDocById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDeveLopDocStateISupById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDeveLopDocStateISdownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDeveLopDocOrderByISupById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysDeveLopDocOrderByISdownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDeveLopFirstTypeToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDeveLopSecondTypeToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDeveLopDocToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysDeveLopDocContentToShow(InputObject inputObject, OutputObject outputObject) throws Exception;

}
