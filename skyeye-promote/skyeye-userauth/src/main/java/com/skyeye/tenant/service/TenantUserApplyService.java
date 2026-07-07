/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.TenantUserApply;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: TenantUserApplyService
 * @Description: 用户申请加入租户服务接口
 */
public interface TenantUserApplyService extends SkyeyeBusinessService<TenantUserApply> {

    void applyToJoinTenant(InputObject inputObject, OutputObject outputObject);

    void cancelMyTenantUserApply(InputObject inputObject, OutputObject outputObject);

    void approveTenantUserApply(InputObject inputObject, OutputObject outputObject);

    void rejectTenantUserApply(InputObject inputObject, OutputObject outputObject);

    void queryTenantUserApplyList(InputObject inputObject, OutputObject outputObject);

    void queryMyTenantUserApplyList(InputObject inputObject, OutputObject outputObject);

    /**
     * 批量查询当前员工对各租户的申请状态（待审核优先）
     */
    Map<String, Integer> queryApplyStatusMapByStaffId(String staffId, List<String> tenantIds);

    Map<String, TenantUserApply> queryPendingApplyMapByStaffId(String staffId, List<String> tenantIds);

}
