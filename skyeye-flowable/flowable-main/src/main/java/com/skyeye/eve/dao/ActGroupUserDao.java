/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.entity.ActGroupUser;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ActGroupUserDao
 * @Description: 用户组关联用户管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/3 22:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ActGroupUserDao extends SkyeyeBaseMapper<ActGroupUser> {

    @IgnoreTenant
    List<Map<String, Object>> queryUserInfoOnActGroup(CommonPageInfo commonPageInfo);

    /**
     * 岗位列表（含部门），供设计器按 jobId 选择
     */
    @IgnoreTenant
    List<Map<String, Object>> queryJobListForActiviti(@org.apache.ibatis.annotations.Param("tenantId") String tenantId);

    /**
     * 按部门 + 岗位ID查用户（发起人同部门岗位规则）
     */
    @IgnoreTenant
    List<Map<String, Object>> queryUserByDeptAndJobIds(@org.apache.ibatis.annotations.Param("departmentId") String departmentId,
                                                      @org.apache.ibatis.annotations.Param("jobIds") java.util.List<String> jobIds,
                                                      @org.apache.ibatis.annotations.Param("tenantId") String tenantId);

    /**
     * 按用户ID查员工部门ID
     */
    @IgnoreTenant
    String queryDepartmentIdByUserId(@org.apache.ibatis.annotations.Param("userId") String userId,
                                    @org.apache.ibatis.annotations.Param("tenantId") String tenantId);

    /**
     * 按用户ID查员工ID
     */
    @IgnoreTenant
    String queryStaffIdByUserId(@org.apache.ibatis.annotations.Param("userId") String userId,
                               @org.apache.ibatis.annotations.Param("tenantId") String tenantId);

    /**
     * 按员工ID查直属上级员工ID
     */
    @IgnoreTenant
    String queryLeaderStaffIdByStaffId(@org.apache.ibatis.annotations.Param("staffId") String staffId,
                                      @org.apache.ibatis.annotations.Param("tenantId") String tenantId);

    /**
     * 按员工ID查用户ID与基础信息
     */
    @IgnoreTenant
    Map<String, Object> queryUserBriefByStaffId(@org.apache.ibatis.annotations.Param("staffId") String staffId,
                                               @org.apache.ibatis.annotations.Param("tenantId") String tenantId);

}
