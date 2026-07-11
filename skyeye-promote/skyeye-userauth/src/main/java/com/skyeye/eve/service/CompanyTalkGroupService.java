/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;

/**
 * @ClassName: CompanyTalkGroupService
 * @Description: 群组服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 16:02
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CompanyTalkGroupService extends SkyeyeBusinessService<CompanyTalkGroup> {

    void insertGroupMationToTalk(InputObject inputObject, OutputObject outputObject);

    void queryGroupMemberByGroupId(InputObject inputObject, OutputObject outputObject);

    void queryChatLogByType(InputObject inputObject, OutputObject outputObject);

    void editUserToExitGroup(InputObject inputObject, OutputObject outputObject);

    void editCreateToExitGroup(InputObject inputObject, OutputObject outputObject);

    void editGroupMation(InputObject inputObject, OutputObject outputObject);

    void insertGroupMemberInvite(InputObject inputObject, OutputObject outputObject);

    void handleUserQuitGroup(String userId, String transferUserId);

}
