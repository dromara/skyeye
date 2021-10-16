/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface WagesSocialSecurityFundService {

    public void queryWagesSocialSecurityFundList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertWagesSocialSecurityFundMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryWagesSocialSecurityFundMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void enableWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void disableWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryWagesSocialSecurityFundMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
}
