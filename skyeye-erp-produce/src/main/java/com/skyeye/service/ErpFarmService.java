/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @author 卫志强
 * @title: ErpFarmService
 * @projectName skyeye-promote
 * @description: TODO
 * @date 2020/8/30 14:18
 */
public interface ErpFarmService {

    public void queryErpFarmList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertErpFarmMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryErpFarmToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryErpFarmListByProcedureId(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void normalErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void rectificationErpFarmMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpFarmListToTable(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryErpFarmProcedureListByIds(InputObject inputObject, OutputObject outputObject) throws Exception;
}
