/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @Author: 卫志强
 * @Description: TODO
 * @Date: 2019/9/16 21:24
 */
public interface SupplierService {
    public void querySupplierByList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertSupplier(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void querySupplierById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteSupplierById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editSupplierById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editSupplierByEnabled(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editSupplierByNotEnabled(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void querySupplierByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySupplierListTableToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;

}
