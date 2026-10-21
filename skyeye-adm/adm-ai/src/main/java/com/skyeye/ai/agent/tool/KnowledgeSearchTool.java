/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.tool;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.agent.AiAgentTool;
import com.skyeye.ai.agent.AiAgentToolContext;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.factory.AiFactory;
import com.skyeye.ai.core.knowledge.AiKnowledgeClient;
import com.skyeye.exception.CustomException;
import com.skyeye.key.entity.AiApiKey;
import com.skyeye.knowledge.util.KnowledgeTenantFilterHelper;
import com.skyeye.role.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 按问题检索角色绑定知识库片段（向量检索，非数目统计）。
 */
@Component
public class KnowledgeSearchTool implements AiAgentTool {

    private static final int DEFAULT_TOP_N = 8;
    private static final int TENANT_TOP_N = 16;
    private static final int MAX_RESULT_CHARS = 8000;

    @Autowired
    private AiFactory aiFactory;

    @Override
    public String name() {
        return "knowledge_search";
    }

    @Override
    public String description() {
        return "检索角色绑定的 AI 知识库文档片段，适用于制度、说明、操作手册等文档问答。"
            + "不要用本工具回答业务笔数/统计数字；数目请用业务查询工具或 knowledge_doc_stats。";
    }

    @Override
    public JSONObject parametersSchema() {
        return JSONUtil.createObj()
            .set("type", "object")
            .set("properties", JSONUtil.createObj()
                .set("query", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", "检索关键词或用户原问题"))
                .set("topN", JSONUtil.createObj()
                    .set("type", "integer")
                    .set("description", "返回片段条数，默认 8，最大 16")))
            .set("required", new cn.hutool.json.JSONArray().put("query"));
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        Role role = context.getRole();
        AiApiKey apiKey = context.getApiKey();
        if (role == null || StrUtil.isBlank(role.getKnowledgeId())) {
            return JSONUtil.createObj().set("ok", false).set("error", "当前角色未绑定知识库").toString();
        }
        if (apiKey == null || StrUtil.isBlank(apiKey.getPlatformKnowledgeId())) {
            return JSONUtil.createObj().set("ok", false).set("error", "未配置平台知识库 ID").toString();
        }
        String query = args == null ? null : args.getStr("query");
        if (StrUtil.isBlank(query)) {
            query = context.getUserQuery();
        }
        if (StrUtil.isBlank(query)) {
            return JSONUtil.createObj().set("ok", false).set("error", "query 不能为空").toString();
        }
        int topN = args == null ? DEFAULT_TOP_N : args.getInt("topN", DEFAULT_TOP_N);
        if (topN <= 0) {
            topN = DEFAULT_TOP_N;
        }
        if (topN > TENANT_TOP_N) {
            topN = TENANT_TOP_N;
        }
        String searchQuery = query;
        if (context.isTenantEnable() && StrUtil.isNotBlank(context.getTenantId())) {
            searchQuery = query + " 租户ID " + context.getTenantId();
            if (topN < TENANT_TOP_N) {
                topN = TENANT_TOP_N;
            }
        }
        try {
            AiPlatformEnum platform = AiPlatformEnum.getName(apiKey.getPlatform());
            AiKnowledgeClient client = aiFactory.getKnowledgeClient(platform);
            if (client.useNativeAppKnowledge()) {
                return JSONUtil.createObj().set("ok", false)
                    .set("error", "当前平台知识库由应用侧托管，无需本地检索").toString();
            }
            String knowledgeText = client.search(apiKey.toAiKnowledgeConfig(), searchQuery, topN);
            knowledgeText = KnowledgeTenantFilterHelper.filterKnowledgeText(
                knowledgeText, context.isTenantEnable(), context.getTenantId());
            if (StrUtil.isBlank(knowledgeText)) {
                return JSONUtil.createObj().set("ok", true).set("snippets", "").set("message", "未检索到相关片段").toString();
            }
            if (knowledgeText.length() > MAX_RESULT_CHARS) {
                knowledgeText = knowledgeText.substring(0, MAX_RESULT_CHARS) + "\n...(truncated)";
            }
            return JSONUtil.createObj()
                .set("ok", true)
                .set("snippets", knowledgeText)
                .toString();
        } catch (CustomException e) {
            return JSONUtil.createObj().set("ok", false).set("error", e.getMessage()).toString();
        } catch (Exception e) {
            return JSONUtil.createObj().set("ok", false)
                .set("error", StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()))
                .toString();
        }
    }
}
