/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: AccountService
 * @Description: 财务账户服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/24 21:55
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
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
