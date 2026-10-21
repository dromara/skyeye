/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.core.doubao.DouBaoChatClient;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.factory.AiFactory;
import com.skyeye.exception.CustomException;
import com.skyeye.key.entity.AiApiKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 豆包 Agent：tools 非流式循环 → 最终流式回答。
 * 工具使用规则写在 AI 角色 {@code prompt}（system）里，不再单独注入。
 */
@Component
public class DouBaoAgentRunner {

    public static final int MAX_TOOL_ROUNDS = 3;
    public static final int MAX_TOOL_RESULT_CHARS = 8000;

    @Autowired
    private AiFactory aiFactory;

    @Autowired
    private AiAgentToolRegistry toolRegistry;

    /**
     * @param baseMessages 已含 system（角色 prompt）/ history / user
     * @param onStatus     状态文案（如「正在检索知识库…」），可空
     * @param listener     最终流式回调
     */
    public void run(AiApiKey apiKey, List<Map<String, String>> baseMessages, List<String> images,
                    AiAgentToolContext context, Consumer<String> onStatus,
                    DouBaoChatClient.StreamListener listener) {
        DouBaoChatClient client = (DouBaoChatClient) aiFactory.getDefaultChatModel(AiPlatformEnum.DOU_BAO, apiKey);
        if (toolRegistry.isEmpty()) {
            client.streamChat(baseMessages, images, listener);
            return;
        }
        List<Object> messages = new ArrayList<>();
        if (baseMessages != null) {
            messages.addAll(baseMessages);
        }
        JSONArray tools = toolRegistry.toOpenAiTools();
        for (int round = 0; round < MAX_TOOL_ROUNDS; round++) {
            DouBaoChatClient.ChatToolsResult result = client.chatWithTools(messages, tools);
            if (!result.hasToolCalls()) {
                if (StrUtil.isNotBlank(result.getContent())) {
                    // 模型已在非流式给出全文：拆成一次 delta 推前端
                    listener.onDelta(result.getContent(), true);
                    return;
                }
                break;
            }
            JSONObject assistantMsg = result.getRawAssistantMessage();
            if (assistantMsg == null) {
                assistantMsg = buildAssistantToolMessage(result);
            }
            messages.add(assistantMsg);
            for (DouBaoChatClient.ToolCall call : result.getToolCalls()) {
                String status = statusForTool(call.getName());
                if (onStatus != null && StrUtil.isNotBlank(status)) {
                    onStatus.accept(status);
                }
                String toolResult = executeTool(call, context);
                messages.add(JSONUtil.createObj()
                    .set("role", "tool")
                    .set("tool_call_id", StrUtil.blankToDefault(call.getId(), "call_" + round))
                    .set("content", toolResult));
            }
        }
        client.streamChatMessages(messages, images, listener);
    }

    private JSONObject buildAssistantToolMessage(DouBaoChatClient.ChatToolsResult result) {
        JSONArray toolCalls = new JSONArray();
        if (result.getToolCalls() != null) {
            for (DouBaoChatClient.ToolCall call : result.getToolCalls()) {
                toolCalls.add(JSONUtil.createObj()
                    .set("id", call.getId())
                    .set("type", StrUtil.blankToDefault(call.getType(), "function"))
                    .set("function", JSONUtil.createObj()
                        .set("name", call.getName())
                        .set("arguments", StrUtil.blankToDefault(call.getArguments(), "{}"))));
            }
        }
        return JSONUtil.createObj()
            .set("role", "assistant")
            .set("content", result.getContent())
            .set("tool_calls", toolCalls);
    }

    private String executeTool(DouBaoChatClient.ToolCall call, AiAgentToolContext context) {
        String name = call == null ? null : call.getName();
        AiAgentTool tool = toolRegistry.get(name);
        if (tool == null) {
            return JSONUtil.createObj().set("ok", false).set("error", "未知工具: " + name).toString();
        }
        JSONObject args;
        try {
            args = JSONUtil.parseObj(StrUtil.blankToDefault(call.getArguments(), "{}"));
        } catch (Exception e) {
            args = JSONUtil.createObj();
        }
        try {
            String result = tool.execute(args, context);
            if (result != null && result.length() > MAX_TOOL_RESULT_CHARS) {
                return result.substring(0, MAX_TOOL_RESULT_CHARS) + "\n...(truncated)";
            }
            return StrUtil.blankToDefault(result, "{}");
        } catch (CustomException e) {
            return JSONUtil.createObj().set("ok", false).set("error", e.getMessage()).toString();
        } catch (Exception e) {
            return JSONUtil.createObj().set("ok", false)
                .set("error", StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()))
                .toString();
        }
    }

    private String statusForTool(String toolName) {
        if ("knowledge_search".equals(toolName)) {
            return "正在检索知识库...";
        }
        if ("knowledge_doc_stats".equals(toolName)) {
            return "正在查询知识库文档统计...";
        }
        if (toolName != null && toolName.startsWith("biz_")) {
            return "正在查询业务数据...";
        }
        return "正在调用工具...";
    }
}
