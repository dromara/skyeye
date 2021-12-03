/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ActGroupUserDao
 * @Description: 工作流用户组与用户之间的关系
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/3 22:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ActGroupUserDao {

    List<Map<String, Object>> queryUserListToActiviti(Map<String, Object> map) throws Exception;

    List<Map<String, Object>> queryUserListToActivitiByGroup(Map<String, Object> parmter) throws Exception;

    List<Map<String, Object>> queryActUserList(Map<String, Object> map) throws Exception;

    List<Map<String, Object>> queryActUserGroupList(Map<String, Object> map) throws Exception;

    List<Map<String, Object>> queryActGroupListByUserId(Map<String, Object> user) throws Exception;

    int insertActGroupUserByGroupId(List<Map<String, Object>> beans) throws Exception;

    int deleteActGroupUserByGroupId(Map<String, Object> map) throws Exception;

    int deleteActGroupUserByGroupIdAndUserId(Map<String, Object> map) throws Exception;

    List<Map<String, Object>> selectUserInfoOnActGroup(Map<String, Object> map) throws Exception;

    Map<String, Object> queryUserIsInActGroup(Map<String, Object> map) throws Exception;

    List<Map<String, Object>> queryActGroupUserByGroupId(@Param("groupIds") List<String> groupIds) throws Exception;

}
