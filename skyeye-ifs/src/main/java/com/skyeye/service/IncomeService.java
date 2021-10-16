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
public interface IncomeService {
    public void queryIncomeByList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertIncome(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryIncomeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editIncomeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteIncomeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryIncomeByDetail(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception;
}
