/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.Tenant;

import java.util.List;

/**
 * @ClassName: TenantService
 * @Description: 租户服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/28 20:14
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TenantService extends SkyeyeBusinessService<Tenant> {

    void editTenantAccountNumber(String tenantId, Integer accountNumber);

    /**
     * 将租户标记为「存在已审批通过的应用购买订单」（订单终审通过时调用）。
     *
     * @param tenantId 租户 id
     */
    void markHasPassedAppBuyOrder(String tenantId);

    void queryAllTenantList(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据租户id查询所有菜单id列表
     *
     * @param tenantId 租户id
     * @param type     菜单类型 {@link com.skyeye.tenant.classenum.TenantAppMenuType}
     * @return
     */
    List<String> queryAllMenuListByTenantId(String tenantId, Integer type);

    void checkTenantAccountNum(String tenantId);

    void queryAllTenantListByKeyword(InputObject inputObject, OutputObject outputObject);

    /**
     * 查询当前租户信息（租户管理员可获取完整信息，非管理员仅返回 isAdmin=0）
     */
    void queryCurrentTenantInfo(InputObject inputObject, OutputObject outputObject);

    /**
     * 租户管理员更新当前租户基本信息（名称、Logo、描述）
     */
    void updateCurrentTenantInfo(InputObject inputObject, OutputObject outputObject);

    /**
     * 当前登录用户自助创建组织，并自动成为组织管理员
     */
    void createCurrentTenant(InputObject inputObject, OutputObject outputObject);

    /**
     * 搜索允许被加入的组织（登录用户）
     */
    void searchSearchableTenantList(InputObject inputObject, OutputObject outputObject);

    /**
     * 统计指定用户自助创建的组织数量（按组织类型，仅用户自助创建来源计入）
     */
    int countUserSelfCreatedTenantByOrgType(String userId, Integer orgType);

    /**
     * 校验当前用户是否可自助创建指定类型的组织
     */
    void assertCanUserCreateTenant(String userId, Integer orgType);

    /**
     * 组织管理员解散当前组织（仅用户自助创建），并发送 MQ 异步清理关联数据。
     * <p>同步清理：成员关系、应用关联、租户主表；异步清理：工作流、群聊等。</p>
     */
    void dissolveCurrentTenant(InputObject inputObject, OutputObject outputObject);

}
