/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotLevelVal;

import java.util.List;

/**
 * @ClassName: DepotLevelValService
 * @Description: 仓库级别的值服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/6 10:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DepotLevelValService extends SkyeyeBusinessService<DepotLevelVal> {

    List<DepotLevelVal> queryDepotLevelValListByParentId(String parentId);

    void deleteDepotLevelValListByDepotLevelId(String... depotLevelId);

    void batchGenerateDepotLevelVal(InputObject inputObject, OutputObject outputObject);

    /**
     * 按仓库查询全部库位值（含各层级，不限 parentId=0）
     */
    void queryAllDepotLevelValByDepotId(InputObject inputObject, OutputObject outputObject);
}
