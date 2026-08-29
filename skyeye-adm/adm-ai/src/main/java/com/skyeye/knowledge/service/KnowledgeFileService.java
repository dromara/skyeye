/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.key.entity.AiApiKey;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.entity.KnowledgeFile;

import java.util.List;

public interface KnowledgeFileService extends SkyeyeBusinessService<KnowledgeFile> {

    List<KnowledgeFile> selectByKnowledgeId(String knowledgeId);

    List<KnowledgeFile> selectNeedSync(String knowledgeId);

    int syncPendingFiles(Knowledge knowledge, AiApiKey apiKey);

    void deleteByKnowledgeId(String knowledgeId);

}
