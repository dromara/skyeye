/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CommonService {

	public void uploadFile(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void uploadFileBase64(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void downloadFileByJsonData(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySysWinMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryAllSysUserIsIncumbency(InputObject inputObject, OutputObject outputObject) throws Exception;
	
}
