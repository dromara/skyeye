/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.material.entity.unit.MaterialUnitGroup;

/**
 * @ClassName: MaterialUnitGroupService
 * @Description: 计量单位分组服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/23 15:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MaterialUnitGroupService extends SkyeyeBusinessService<MaterialUnitGroup> {

    void queryAllMaterialUnitList(InputObject inputObject, OutputObject outputObject);

    /**
     * 商城个人门店：确保有可用的默认计量单位组（件）
     */
    MaterialUnitGroup ensureDefaultPieceGroup(String userId);

}
