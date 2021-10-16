/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface WagesFieldTypeService {

    public void queryWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertWagesFieldTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryWagesFieldTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void enableWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void disableWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryEnableWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void querySysWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;
}
