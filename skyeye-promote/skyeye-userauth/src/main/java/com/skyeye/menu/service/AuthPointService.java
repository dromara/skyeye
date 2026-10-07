/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.menu.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.menu.entity.AuthPoint;

/**
 * @ClassName: AuthPointService
 * @Description: 菜单权限点管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 21:24
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface AuthPointService extends SkyeyeBusinessService<AuthPoint> {

    /**
     * 将某菜单下全部权限点迁移到目标菜单，可选名称前缀。
     */
    void migrateAuthPoint(InputObject inputObject, OutputObject outputObject);

}
