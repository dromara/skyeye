/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.knowledge.entity.KnowledgeSyncHistory;

public interface KnowledgeSyncHistoryService extends SkyeyeBusinessService<KnowledgeSyncHistory> {

    void saveHistory(String knowledgeId, Integer triggerType, Integer status, Integer syncCount,
                     String startTime, String endTime, String errorMsg);

    /** 创建「同步中」记录，返回历史 id */
    String createRunningHistory(String knowledgeId, Integer triggerType, String startTime);

    /** 更新同步结果（成功/失败） */
    void finishHistory(String historyId, Integer status, Integer syncCount, String endTime, String errorMsg);

    /** 同一知识库是否已有进行中的同步 */
    boolean hasRunning(String knowledgeId);

    void deleteByKnowledgeId(String knowledgeId);

}
