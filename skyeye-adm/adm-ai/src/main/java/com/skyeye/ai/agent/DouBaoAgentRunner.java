/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.agent.tool.DirectReplyTool;
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
 * <p>
 * 闲聊路径统一 {@code tool_choice=required}：由模型在业务工具与 {@link DirectReplyTool} 之间路由，
 * 不做关键词匹配。工具使用细则写在 AI 角色 prompt 与各工具 description 里。
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
        // 表单/布局 Assist 等非闲聊：不挂 tools，避免干扰结构化输出
        if (toolRegistry.isEmpty() || !isChatBiz(context)) {
            client.streamChat(baseMessages, images, listener);
            return;
        }
        List<Object> messages = new ArrayList<>();
        if (baseMessages != null) {
            messages.addAll(baseMessages);
        }
        JSONArray tools = toolRegistry.toOpenAiTools();
        for (int round = 0; round < MAX_TOOL_ROUNDS; round++) {
            // 必须选一个工具：查数 or direct_reply，避免「请稍等」空话当最终答案
            DouBaoChatClient.ChatToolsResult result = client.chatWithTools(messages, tools, "required");
            if (!result.hasToolCalls()) {
                // 个别网关忽略 required 时的兜底：有文案则当最终答，否则进入流式
                if (StrUtil.isNotBlank(result.getContent())) {
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
            boolean onlyDirect = true;
            for (DouBaoChatClient.ToolCall call : result.getToolCalls()) {
                if (!DirectReplyTool.NAME.equals(call.getName())) {
                    onlyDirect = false;
                }
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
            // 只选了 direct_reply：结束工具循环，流式生成最终回答
            if (onlyDirect) {
                break;
            }
        }
        client.streamChatMessages(messages, images, listener);
    }

    /**
     * 闲聊 / 旧聊天页（bizType 为空）才启用 Agent 工具
     */
    private boolean isChatBiz(AiAgentToolContext context) {
        if (context == null) {
            return true;
        }
        String bizType = context.getBizType();
        return bizType == null || "chat".equals(bizType);
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
        if (DirectReplyTool.NAME.equals(toolName)) {
            return null;
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
