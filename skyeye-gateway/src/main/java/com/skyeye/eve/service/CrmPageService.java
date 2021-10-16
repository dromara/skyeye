/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CrmPageService {

	public void queryInsertNumByYear(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCustomNumByOtherType(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCustomDocumentaryType(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryNewContractNum(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryNewDocumentaryNum(InputObject inputObject, OutputObject outputObject) throws Exception;

}
