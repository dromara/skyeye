/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupInvite;

import java.util.List;

/**
 * @ClassName: CompanyTalkGroupInviteService
 * @Description: 群组邀请信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 16:58
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CompanyTalkGroupInviteService extends SkyeyeBusinessService<CompanyTalkGroupInvite> {

    void saveList(String groupId, List<String> userIds, String createId);

    void addInviteList(String groupId, List<String> userIds, String createId);

    void deleteByGroupId(String groupId);

    List<CompanyTalkGroupInvite> selectByGroupId(String groupId);

    void queryGroupInvitationMation(InputObject inputObject, OutputObject outputObject);

    void editAgreeInGroupInvitationMation(InputObject inputObject, OutputObject outputObject);

    void editRefuseInGroupInvitationMation(InputObject inputObject, OutputObject outputObject);

    boolean checkGroupInvitationMationByUserId(String userId, String groupId);

}
