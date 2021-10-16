/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ProProjectDiscussService {
	
	public void queryProProjectDiscussList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertProProjectDiscuss(InputObject inputObject, OutputObject object) throws Exception;

    public void insertProProjectDiscussReply(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteProProjectDiscussById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryProProjectDiscussMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllDiscussList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
