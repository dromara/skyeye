/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 收集全部 {@link AiAgentTool} Bean，并组装 OpenAI tools 数组。
 */
@Component
public class AiAgentToolRegistry {

    private final Map<String, AiAgentTool> toolsByName;

    public AiAgentToolRegistry(List<AiAgentTool> tools) {
        Map<String, AiAgentTool> map = new LinkedHashMap<>();
        if (tools != null) {
            for (AiAgentTool tool : tools) {
                if (tool == null || StrUtil.isBlank(tool.name())) {
                    continue;
                }
                map.put(tool.name(), tool);
            }
        }
        this.toolsByName = Collections.unmodifiableMap(map);
    }

    public AiAgentTool get(String name) {
        return toolsByName.get(name);
    }

    public List<AiAgentTool> listAll() {
        return new ArrayList<>(toolsByName.values());
    }

    /** OpenAI chat/completions 的 tools 字段 */
    public JSONArray toOpenAiTools() {
        JSONArray arr = new JSONArray();
        for (AiAgentTool tool : toolsByName.values()) {
            JSONObject fn = JSONUtil.createObj()
                .set("name", tool.name())
                .set("description", tool.description())
                .set("parameters", tool.parametersSchema());
            arr.add(JSONUtil.createObj().set("type", "function").set("function", fn));
        }
        return arr;
    }

    public boolean isEmpty() {
        return toolsByName.isEmpty();
    }
}
