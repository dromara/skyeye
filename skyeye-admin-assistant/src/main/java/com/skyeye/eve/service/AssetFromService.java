/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AssetFromService
 * @Description: 资产来源服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 16:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetFromService {

    public void selectAllAssetFromMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertAssetFromMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteAssetFromById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetFromMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetFromMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void selectAllAssetFromToChoose(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetFromSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetFromSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetFromUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetFromDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;
    
}
