/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @Author: 奈何繁华如云烟
 * @Description: TODO
 * @Date: 2019/10/6 15:42
 */
public interface AccountService {

    public void queryAccountByList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertAccount(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAccountById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteAccountById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAccountById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAccountByIdAndIsDefault(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAccountByIdAndInfo(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAccountStreamById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAccountListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception;
}
