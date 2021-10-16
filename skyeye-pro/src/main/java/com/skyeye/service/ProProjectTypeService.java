/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ProProjectTypeService {

    public void insertProProjectType(InputObject inputObject, OutputObject object) throws Exception;

    public void queryProProjectTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editProProjectTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteProProjectTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryProProjectTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
