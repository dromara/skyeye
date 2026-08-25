/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.knowledge.service.KnowledgeService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 按知识库上的巡检式频次执行同步。XXL 建议每分钟调度一次。
 */
@Component
public class KnowledgeSyncXxlJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeSyncXxlJob.class);

    @Autowired
    private KnowledgeService knowledgeService;

    @Autowired
    private ITenantService iTenantService;

    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    @XxlJob("syncAiKnowledgeJob")
    public void syncAiKnowledgeJob() {
        try {
            if (tenantEnable) {
                List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
                if (CollectionUtil.isEmpty(tenantList)) {
                    return;
                }
                tenantList.forEach(tenant -> {
                    TenantContext.setTenantId(tenant.get("id").toString());
                    knowledgeService.syncDueKnowledges();
                });
            } else {
                knowledgeService.syncDueKnowledges();
            }
        } catch (Exception e) {
            LOGGER.warn("知识库定时同步失败: {}", e.getMessage());
        }
    }

}
