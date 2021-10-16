/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AssetArticlesPurchaseService
 * @Description: 用品采购申请服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 11:40
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetArticlesApplyPurchaseService {

    public void insertAssetArticlesListToPurchase(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMyPurchaseAssetArticlesMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetArticlesListPurchaseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetArticlesUseToPurchaseSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetArticlesListPurchaseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetArticlesListToPurchaseById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetArticlesPurchaseToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetArticlesListToPurchaseByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetArticlesPurchaseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
