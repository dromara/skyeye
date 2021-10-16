/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CrmDocumentaryTypeService {

    public void insertCrmDocumentaryType(InputObject inputObject, OutputObject object) throws Exception;

    public void queryCrmDocumentaryTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editCrmDocumentaryTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteCrmDocumentaryTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryCrmDocumentaryTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
