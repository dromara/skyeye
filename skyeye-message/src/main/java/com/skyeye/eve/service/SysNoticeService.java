/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysNoticeService {

	public void querySysNoticeList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertSysNoticeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateUpSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateDownSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectSysNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysNoticeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysNoticeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysNoticeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysNoticeTimeUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysNoticeDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUserReceivedSysNotice(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryReceivedSysNoticeDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
