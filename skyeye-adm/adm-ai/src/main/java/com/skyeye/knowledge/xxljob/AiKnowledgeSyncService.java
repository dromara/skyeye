/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.xxljob;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.service.KnowledgeService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 由 SysQuartz 按知识库注册的 XXL 子任务：根据 objectId(知识库 id) 触发同步。
 * 任务参数为 JSON：objectId、userId、tenantId。
 * Handler 名须与 {@code QuartzConstants.AI_KNOWLEDGE_SYNC.serviceName} 一致。
 */
@Slf4j
@Component
public class AiKnowledgeSyncService {

    @Autowired
    private KnowledgeService knowledgeService;

    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    @XxlJob("aiKnowledgeSyncService")
    public void syncAiKnowledge() {
        String param = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(param)) {
            log.warn("AI知识库同步：执行参数为空");
            return;
        }
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String knowledgeId = paramMap.get("objectId");
        if (StrUtil.isBlank(knowledgeId)) {
            log.warn("AI知识库同步：objectId 为空");
            return;
        }
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            // selectById 会脱敏密码，这里只做存在/启用校验；真正同步走 syncKnowledgeById
            Knowledge knowledge = knowledgeService.selectById(knowledgeId);
            if (ObjectUtil.isEmpty(knowledge) || StrUtil.isBlank(knowledge.getId())) {
                log.warn("AI知识库[{}]不存在，跳过同步", knowledgeId);
                return;
            }
            if (!EnableEnum.ENABLE_USING.getKey().equals(knowledge.getEnabled())) {
                log.warn("AI知识库[{}]未启用，跳过同步", knowledgeId);
                return;
            }
            knowledgeService.syncKnowledgeById(knowledgeId);
        } catch (Exception e) {
            log.warn("AI知识库[{}]定时同步失败", knowledgeId, e);
        } finally {
            if (tenantEnable) {
                TenantContext.clear();
            }
        }
    }

}
