/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.TenantAppLink;

import java.util.List;

/**
 * @ClassName: TenantAppLinkService
 * @Description: 租户与应用的关系管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 20:44
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TenantAppLinkService extends SkyeyeBusinessService<TenantAppLink> {

    void saveTenantAppLink(String tenantId, String appId, Integer year);

    List<TenantAppLink> selectByTenantId(String tenantId);

    void queryTenantAppLinkList(InputObject inputObject, OutputObject outputObject);

    /**
     * 按购买租户删除全部应用关联。
     */
    void deleteByTenantId(String tenantId);
}
