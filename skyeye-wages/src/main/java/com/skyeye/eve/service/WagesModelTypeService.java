/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface WagesModelTypeService {

    public void queryWagesModelTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertWagesModelTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryWagesModelTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void enableWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void disableWagesModelTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryEnableWagesModelTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;
}
