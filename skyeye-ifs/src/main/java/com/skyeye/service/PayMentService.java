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
public interface PayMentService {
	
    public void queryPayMentByList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertPayMent(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryPayMentToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editPayMentById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deletePayMentById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryPayMentByDetail(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception;
    
}
