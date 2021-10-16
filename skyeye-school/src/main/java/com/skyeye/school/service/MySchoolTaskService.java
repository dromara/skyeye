/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface MySchoolTaskService {

	public void queryMyNowLeadClassList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyWaitMarkingList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyEndMarkingList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
