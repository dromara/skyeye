/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupUser;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CompanyTalkGroupUserService
 * @Description: 群组成员服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 17:16
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CompanyTalkGroupUserService extends SkyeyeBusinessService<CompanyTalkGroupUser> {

    List<CompanyTalkGroupUser> selectByGroupId(String groupId);

    /**
     * 获取群成员 userId 列表（带 Redis 缓存，成员变更时自动失效）
     */
    List<String> selectMemberUserIdsByGroupIdWithCache(String groupId);

    /**
     * 清除群成员相关缓存（成员列表 + userId 列表）
     */
    void evictGroupMemberCache(String groupId);

    long countByGroupId(String groupId);

    Map<String, String> batchCheckGroupUserIsExit(List<String> groupId, String userId);

    boolean checkGroupUserIsExit(String groupId, String userId);

    void deleteByGroupIdAndUserId(String groupId, String userId);

}
