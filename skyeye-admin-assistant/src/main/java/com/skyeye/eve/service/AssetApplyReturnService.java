/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AssetApplyReturnService
 * @Description: 资产规划单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/20 22:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetApplyReturnService {

    public void queryMyReturnAssetMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryMyUnReturnAssetListByTypeId(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertAssetListToReturn(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetReturnToSubApproval(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetListReturnDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetReturnToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetListReturnToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetListToReturnById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateAssetListToReturnByIdInProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetReturnToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

}
