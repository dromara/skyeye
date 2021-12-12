/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AssetPurchaseService
 * @Description: 资产采购单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 23:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetApplyPurchaseService {

    public void queryMyPurchaseAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertAssetListToPurchase(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetPurchaseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetListPurchaseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetPurchaseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetListPurchaseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetListToPurchaseById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetPurchaseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
