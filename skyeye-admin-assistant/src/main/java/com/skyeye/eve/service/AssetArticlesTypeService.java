/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AssetArticlesTypeService
 * @Description: 用品分类服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetArticlesTypeService {

    public void queryAssetArticlesTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void insertAssetArticlesType(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void deleteAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateUpAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void updateDownAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void selectAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetArticlesTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetArticlesTypeOrderUpById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editAssetArticlesTypeOrderDownById(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryAssetArticlesTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception;

}
