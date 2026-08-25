/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.knowledge.entity.KnowledgeSyncHistory;

public interface KnowledgeSyncHistoryService extends SkyeyeBusinessService<KnowledgeSyncHistory> {

    void saveHistory(String knowledgeId, Integer triggerType, Integer status, Integer syncCount,
                     String startTime, String endTime, String errorMsg);

    void deleteByKnowledgeId(String knowledgeId);

}
