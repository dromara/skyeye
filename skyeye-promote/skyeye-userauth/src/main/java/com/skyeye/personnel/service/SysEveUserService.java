/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.entity.SysEveUser;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveUserService
 * @Description: 用户管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 16:07
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserService extends SkyeyeBusinessService<SysEveUser> {

    void querySysUserList(InputObject inputObject, OutputObject outputObject);

    void editSysUserLockStateToLockById(InputObject inputObject, OutputObject outputObject);

    void editSysUserLockStateToUnLockById(InputObject inputObject, OutputObject outputObject);

    void editSysUserPasswordMationById(InputObject inputObject, OutputObject outputObject);

    void queryUserToLogin(InputObject inputObject, OutputObject outputObject);

    void queryUserMationBySession(InputObject inputObject, OutputObject outputObject);

    void deleteUserMationBySession(InputObject inputObject, OutputObject outputObject);

    /**
     * 退出登录
     *
     * @param userId 用户id
     */
    void removeLogin(String userId, boolean removeAll);

    void queryRoleAndBindRoleByUserId(InputObject inputObject, OutputObject outputObject);

    void editRoleIdsByUserId(InputObject inputObject, OutputObject outputObject);

    void queryAllMenuBySession(InputObject inputObject, OutputObject outputObject);

    /**
     * 获取当前登录用户的APP菜单树
     *
     * @param userIdAndType 用户id(含APP标识)
     * @return APP菜单树
     */
    List<Map<String, Object>> queryAppMenuTreeBySession(String userIdAndType);

    void setUserLoginRedisMation(String userId, Map<String, Object> userMation, boolean editAll);

    void insertSysUserMationById(InputObject inputObject, OutputObject outputObject);

    void addNewUser(String currentUserId, SysEveUser sysEveUser);

    void editUserPassword(InputObject inputObject, OutputObject outputObject);

    void queryUserDetailsMationByUserId(InputObject inputObject, OutputObject outputObject);

    void editUserDetailsMationByUserId(InputObject inputObject, OutputObject outputObject);

    void queryAllPeopleToTree(InputObject inputObject, OutputObject outputObject);

    void queryCompanyPeopleToTreeByUserBelongCompany(InputObject inputObject, OutputObject outputObject);

    void queryDepartmentPeopleToTreeByUserBelongDepartment(InputObject inputObject, OutputObject outputObject);

    void queryJobPeopleToTreeByUserBelongJob(InputObject inputObject, OutputObject outputObject);

    void querySimpleDepPeopleToTreeByUserBelongSimpleDep(InputObject inputObject, OutputObject outputObject);

    void queryTalkGroupUserListByUserId(InputObject inputObject, OutputObject outputObject);

    void queryPhoneToLogin(InputObject inputObject, OutputObject outputObject);

    void queryUserMationByOpenId(InputObject inputObject, OutputObject outputObject);

    void insertUserMationByOpenId(InputObject inputObject, OutputObject outputObject);

    void editUserLockState(String id, Integer userLock);

    void resetUserEffectiveDate(InputObject inputObject, OutputObject outputObject);

    void registerUser(InputObject inputObject, OutputObject outputObject);
}
