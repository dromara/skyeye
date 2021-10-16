/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @Author: 卫志强
 * @Description: TODO
 * @Date: 2019/10/20 10:22
 */
public interface ReceivablesService {
	
    public void queryReceivablesByList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertReceivables(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryReceivablesToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editReceivablesById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteReceivablesById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryReceivablesByDetail(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception;
    
}
