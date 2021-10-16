/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface MyHasMationService {

    public void queryMyAssetManagementList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMySealManageList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMyLicenceManageList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMyAssetArticlesList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMyVehicleHistoryList(InputObject inputObject, OutputObject outputObject) throws Exception;
}
