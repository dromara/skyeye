/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ErpMachinService {

	public void queryMachinOrderToList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertMachinOrderMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMachinOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMachinOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteMachinOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMachinOrderDetailMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMachinOrderMationToSubmitById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMachinOrderMationToExamineById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMachinStateIsPassOrderListByDepartmentId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMachinStateIsPassNoComplateOrderListByDepartmentId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMachinStateIsPassOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editMachinStateMationByChildId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
