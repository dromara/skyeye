/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ProCostExpenseTypeService {

    public void insertProCostExpenseType(InputObject inputObject, OutputObject object) throws Exception;

    public void queryProCostExpenseTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editProCostExpenseTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteProCostExpenseTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryProCostExpenseTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
