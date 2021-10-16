/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysNoticeTypeService {

	public void querySysNoticeTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertSysNoticeTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateUpSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateDownSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void selectSysNoticeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysNoticeTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysNoticeTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editSysNoticeTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryFirstSysNoticeTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllFirstSysNoticeTypeStateList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySecondSysNoticeTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
