/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CrmCustomerGroupService {

    public void insertCustomerGroup(InputObject inputObject, OutputObject object) throws Exception;

    public void queryCustomerGroupList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryStateUpList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editCustomerGroupById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteCustomerGroupById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryCustomerGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
