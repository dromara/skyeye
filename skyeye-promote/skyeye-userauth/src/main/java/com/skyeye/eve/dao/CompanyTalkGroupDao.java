/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CompanyTalkGroupDao
 * @Description: 群组信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:52
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CompanyTalkGroupDao extends SkyeyeBaseMapper<CompanyTalkGroup> {

    @IgnoreTenant
    List<Map<String, Object>> queryGroupMemberByGroupId(@Param("groupId") String groupId,
                                                        @Param("tenantId") String tenantId);

    List<Map<String, Object>> queryGroupMemberByGroupIdAndNotThisUser(Map<String, Object> map);

    @IgnoreTenant
    List<Map<String, Object>> queryChatLogByPerToPer(Map<String, Object> map);

    @IgnoreTenant
    List<Map<String, Object>> queryChatLogByPerToGroup(Map<String, Object> map);

}
