/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CheckWorkReasonService {

	public void queryCheckWorkReasonList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCheckWorkReasonMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateUpCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateDownCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCheckWorkReasonMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCheckWorkReasonUpMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCheckWorkReasonDownMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCheckWorkReasonUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
