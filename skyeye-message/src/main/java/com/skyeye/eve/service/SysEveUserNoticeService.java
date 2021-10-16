/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysEveUserNoticeService {

	public void getNoticeListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void getAllNoticeListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editNoticeMationByIds(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteNoticeMationByIds(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
