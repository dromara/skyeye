/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SealServicePhoneService {

	public void queryNumberInEveryState(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSealSeServiceWaitToSignonMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryFeedBackList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllPartsList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
