/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.knowledge.entity.KnowledgeDoc;

import java.util.List;
import java.util.Map;

public interface KnowledgeDocService extends SkyeyeBusinessService<KnowledgeDoc> {

    void deleteByKnowledgeId(String knowledgeId);

    void deleteByKnowledgeAndTable(String knowledgeId, String sourceTable);

    KnowledgeDoc selectBySource(String knowledgeId, String sourceTable, String sourceId);

    /**
     * 仅从指定知识库检索，拼成对话上下文。
     */
    String searchContext(String knowledgeId, String queryText, int topN);

    /**
     * @param fullSync    全量模式（按源主键直接写入，不做增量 upsert）
     * @param clearBefore 是否先清空该表已同步文档（仅全量首批为 true）
     */
    int saveSyncedRows(String knowledgeId, String sourceTable, List<Map<String, Object>> rows,
                       String idField, String titleField, List<String> contentFields,
                       boolean fullSync, boolean clearBefore);

}
