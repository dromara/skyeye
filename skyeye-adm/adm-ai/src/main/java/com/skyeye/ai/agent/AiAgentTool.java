/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent;

import cn.hutool.json.JSONObject;

/**
 * Agent 可调用工具。实现类注册为 Spring Bean 后由 {@link AiAgentToolRegistry} 收集。
 */
public interface AiAgentTool {

    /**
     * 英文蛇形，如 knowledge_search
     */
    String name();

    /**
     * 给模型看的用途说明
     */
    String description();

    /**
     * OpenAI tools.function.parameters 的 JSON Schema（不含外层 type=function）。
     */
    JSONObject parametersSchema();

    /**
     * 执行工具，返回写入 messages 的字符串（建议 JSON）。
     */
    String execute(JSONObject args, AiAgentToolContext context);
}
