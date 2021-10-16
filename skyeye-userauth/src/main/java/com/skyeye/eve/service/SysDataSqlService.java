/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface SysDataSqlService {

	public void querySysDataSqlBackupsList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllTableMationList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertTableBackUps(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertTableReduction(InputObject inputObject, OutputObject outputObject) throws Exception;

}
