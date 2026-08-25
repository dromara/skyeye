/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.knowledge.entity.Knowledge;

public interface KnowledgeService extends SkyeyeBusinessService<Knowledge> {

    void testDbConnection(InputObject inputObject, OutputObject outputObject);

    void queryDbTables(InputObject inputObject, OutputObject outputObject);

    void queryTableColumns(InputObject inputObject, OutputObject outputObject);

    void syncNow(InputObject inputObject, OutputObject outputObject);

    /**
     * 按当前知识库配置执行一次同步（默认手动触发）
     */
    int syncKnowledge(Knowledge knowledge);

    /**
     * 按当前知识库配置执行一次同步
     *
     * @param triggerType 触发方式，参考 KnowledgeSyncTriggerEnum
     */
    int syncKnowledge(Knowledge knowledge, Integer triggerType);

    /**
     * 扫描到期的知识库并同步（定时任务）
     */
    void syncDueKnowledges();

}
