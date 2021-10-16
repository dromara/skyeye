/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SchoolUserService {

	public void queryStuMationToLogin(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryStuUserMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryStuExit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryStuUserMationDetailById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editUserPassword(InputObject inputObject, OutputObject outputObject) throws Exception;

}
