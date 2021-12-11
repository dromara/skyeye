/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AssetArticlesApplyUseService
 * @Description: 用品领用申请服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetArticlesApplyUseService {

    public void queryMyUseAssetArticlesMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertAssetArticlesListToUse(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetArticlesListUseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetArticlesListUseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetArticlesListToUseById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetArticlesUseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetArticlesToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetArticlesUseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
