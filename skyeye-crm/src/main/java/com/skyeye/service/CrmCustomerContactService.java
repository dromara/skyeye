/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CrmCustomerContactService {

	public void queryCustomerContactList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCustomerContactMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCustomerContactMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCustomerContactMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCustomerContactMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
