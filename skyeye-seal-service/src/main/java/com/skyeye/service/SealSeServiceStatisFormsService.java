/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SealSeServiceStatisFormsService {

	public void queryCustomOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUserWorkerOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryWarrantyOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProductTypeOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception;

}
