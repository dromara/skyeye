/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SealSeServiceWorkerService {

	public void queryServiceWorkerList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertServiceWorkerMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteServiceWorkerMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryServiceWorkerMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editServiceWorkerMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryServiceWorkerShowList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryServiceWorkerToMapList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
