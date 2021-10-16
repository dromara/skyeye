/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ErpWorkProcedureTypeService {

	public void queryErpWorkProcedureTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertErpWorkProcedureTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryErpWorkProcedureTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void downErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void upErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteErpWorkProcedureTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryErpWorkProcedureTypeUpMation(InputObject inputObject, OutputObject outputObject) throws Exception;
}
