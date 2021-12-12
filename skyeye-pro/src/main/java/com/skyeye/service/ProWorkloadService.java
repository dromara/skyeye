/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ProWorkloadService {

    public void queryProWorkloadList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertProWorkloadMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllProWorkloadList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProWorkloadToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProWorkloadProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProWorkloadMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProWorkloadMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProWorkloadMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteProWorkloadMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateProWorkloadToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

}
