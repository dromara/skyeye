/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.knowledge.entity.KnowledgeSync;

import java.util.List;

public interface KnowledgeSyncService extends SkyeyeBusinessService<KnowledgeSync> {

    void saveList(String knowledgeId, List<KnowledgeSync> syncList);

    List<KnowledgeSync> selectByKnowledgeId(String knowledgeId);

    void deleteByKnowledgeId(String knowledgeId);

}
