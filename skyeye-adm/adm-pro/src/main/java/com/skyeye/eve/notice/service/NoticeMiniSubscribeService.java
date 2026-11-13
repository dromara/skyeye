/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.notice.entity.NoticeMiniSubscribe;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NoticeMiniSubscribeService extends SkyeyeBusinessService<NoticeMiniSubscribe> {

    /** 当前用户批量上报授权结果（仅 accept/reject/ban） */
    void upsertStatuses(String userId, List<Map<String, Object>> items);

    /** 查询用户仍为 accept 的模板 ID */
    Set<String> listAcceptedTemplateIds(String userId, Collection<String> templateIds);

    /** 发送成功或 43101 后清除 accept */
    void consumeAccept(String userId, String templateId);

}
