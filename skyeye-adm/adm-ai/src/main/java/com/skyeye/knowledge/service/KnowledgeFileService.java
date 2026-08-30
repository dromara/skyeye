/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.key.entity.AiApiKey;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.entity.KnowledgeFile;
import com.skyeye.knowledge.entity.KnowledgeSyncHistoryItem;

import java.util.List;

public interface KnowledgeFileService extends SkyeyeBusinessService<KnowledgeFile> {

    List<KnowledgeFile> selectByKnowledgeId(String knowledgeId);

    List<KnowledgeFile> selectNeedSync(String knowledgeId);

    int syncPendingFiles(Knowledge knowledge, AiApiKey apiKey);

    /**
     * 同步知识库下全部上传文件（含已同步，覆盖重新导入），并返回每个文件的明细
     */
    List<KnowledgeSyncHistoryItem> syncPendingFileItems(Knowledge knowledge, AiApiKey apiKey);

    /**
     * 单独同步一个上传文件（写入同步历史）
     */
    void syncFileById(InputObject inputObject, OutputObject outputObject);

    void deleteByKnowledgeId(String knowledgeId);

}
