/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.tool;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.agent.AiAgentTool;
import com.skyeye.ai.agent.AiAgentToolContext;
import com.skyeye.knowledge.classenum.KnowledgeFileSyncStatusEnum;
import com.skyeye.knowledge.entity.KnowledgeFile;
import com.skyeye.knowledge.service.KnowledgeFileService;
import com.skyeye.role.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询本地知识库上传文件数量与同步状态（结构化计数，不靠向量检索猜）。
 */
@Component
public class KnowledgeDocStatsTool implements AiAgentTool {

    @Autowired
    private KnowledgeFileService knowledgeFileService;

    @Override
    public String name() {
        return "knowledge_doc_stats";
    }

    @Override
    public String description() {
        return "查询当前角色绑定知识库的本地文档数量及同步状态（待同步/已同步/失败）。"
            + "回答「知识库有多少文档」「同步了多少」等问题时必须调用本工具。";
    }

    @Override
    public JSONObject parametersSchema() {
        return JSONUtil.createObj()
            .set("type", "object")
            .set("properties", JSONUtil.createObj())
            .set("required", new cn.hutool.json.JSONArray());
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        Role role = context.getRole();
        if (role == null || StrUtil.isBlank(role.getKnowledgeId())) {
            return JSONUtil.createObj().set("ok", false).set("error", "当前角色未绑定知识库").toString();
        }
        String knowledgeId = role.getKnowledgeId();
        List<KnowledgeFile> files = knowledgeFileService.selectByKnowledgeId(knowledgeId);
        int total = files == null ? 0 : files.size();
        int wait = 0;
        int success = 0;
        int fail = 0;
        if (files != null) {
            for (KnowledgeFile file : files) {
                Integer status = file.getSyncStatus();
                if (KnowledgeFileSyncStatusEnum.SUCCESS.getKey().equals(status)) {
                    success++;
                } else if (KnowledgeFileSyncStatusEnum.FAIL.getKey().equals(status)) {
                    fail++;
                } else {
                    wait++;
                }
            }
        }
        return JSONUtil.createObj()
            .set("ok", true)
            .set("knowledgeId", knowledgeId)
            .set("total", total)
            .set("wait", wait)
            .set("success", success)
            .set("fail", fail)
            .toString();
    }
}
