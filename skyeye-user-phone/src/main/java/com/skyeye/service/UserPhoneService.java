/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface UserPhoneService {

	public void queryPhoneToLogin(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPhoneUserMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPhoneUserMenuAuth(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryPhoneToExit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUserMationByOpenId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertUserMationByOpenId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllPeopleToTree(InputObject inputObject, OutputObject outputObject) throws Exception;

}
