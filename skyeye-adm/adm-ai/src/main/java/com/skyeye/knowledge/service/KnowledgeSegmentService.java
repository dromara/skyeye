/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.knowledge.entity.KnowledgeSegment;

import java.util.List;

public interface KnowledgeSegmentService extends SkyeyeBusinessService<KnowledgeSegment> {

    void deleteByKnowledgeId(String knowledgeId);

    void deleteByDocIds(List<String> docIds);

    void saveForDoc(String knowledgeId, String docId, String docName, String content);

    List<KnowledgeSegment> search(String knowledgeId, String keyword, int topN);

}
