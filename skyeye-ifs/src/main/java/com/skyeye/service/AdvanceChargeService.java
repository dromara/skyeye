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
public interface AdvanceChargeService {
	
    public void queryAdvanceChargeByList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertAdvanceCharge(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAdvanceChargeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAdvanceChargeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteAdvanceChargeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAdvanceChargeByDetail(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception;
    
}
