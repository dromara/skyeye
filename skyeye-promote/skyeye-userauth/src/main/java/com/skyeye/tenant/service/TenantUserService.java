/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.entity.SysEveUserStaff;
import com.skyeye.tenant.entity.TenantUser;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: TenantUserService
 * @Description: 租户下的用户管理服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/26 22:47
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TenantUserService extends SkyeyeBusinessService<TenantUser> {

    void removeTenantUserByStaffId(InputObject inputObject, OutputObject outputObject);

    void exitTenantUser(InputObject inputObject, OutputObject outputObject);

    void queryTenantUserByStaffId(InputObject inputObject, OutputObject outputObject);

    TenantUser queryTenantUserByStaffId(String staffId, String tenantId);

    void editUserStaffActMoneyByStaffId(String staffId, String actMoney);

    void editUserStaffAnnualLeaveByStaffId(String staffId, String quarterYearHour, String annualLeaveStatisTime);

    void editUserStaffHolidayByStaffId(String staffId, String holidayNumber, String holidayStatisTime);

    void editUserStaffRetiredHolidayByStaffId(String staffId, String retiredHolidayNumber, String retiredHolidayStatisTime);

    SysEveUserStaff setThisTenantUserToDefault(SysEveUserStaff sysEveUserStaff);

    Map<String, Object> setThisTenantUserToDefault(Map<String, Object> sysEveUserStaff);

    Map<String, Object> setThisTenantUserToDefault(Map<String, Object> sysEveUserStaff, String pointId);

    List<SysEveUserStaff> setThisTenantUserToDefault(List<SysEveUserStaff> userStaffList);

    List<Map<String, Object>> setThisTenantUserToDefault(List<Map<String, Object>> sysEveUserStaffList, String pointId);

    void addTenantAdminUser(InputObject inputObject, OutputObject outputObject);

    boolean checkStaffIdIsAdmin(String staffId);

    boolean checkStaffIdIsAdmin(TenantUser tenantUser);

    TenantUser getTenantUserByStaffId(String staffId);

    void switchingIdentitiesById(InputObject inputObject, OutputObject outputObject);

    long getTenantUserCountByTenantId(String tenantId);

    void quit(String staffId, String quitTime, String quitReason);

    void queryTenantUserStaffIdByTenantId(InputObject inputObject, OutputObject outputObject);

    void queryTenantUserListByTenantId(InputObject inputObject, OutputObject outputObject);

    /**
     * 解散组织时清理该租户下全部成员，并清理用户缓存。
     */
    void removeAllByTenantId(String tenantId);

}
