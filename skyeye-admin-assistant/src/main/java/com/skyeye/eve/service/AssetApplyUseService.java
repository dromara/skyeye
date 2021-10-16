/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AssetApplyUseService
 * @Description: 资产领用服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 17:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetApplyUseService {

    public void queryMyUseAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertAssetListToUse(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetListUseDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetListUseToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetListToUseById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetUseToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetListToUseByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetUseToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
