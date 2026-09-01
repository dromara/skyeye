/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.*;

/**
 * 把界面积木 / 节点图画编译成模型可读的说明书。
 */
public final class AiSkillBlockCompiler {

    private AiSkillBlockCompiler() {
    }

    public static String compile(String blocksJson) {
        if (StrUtil.isBlank(blocksJson)) {
            return "";
        }
        String trim = blocksJson.trim();
        if (trim.startsWith("{")) {
            return compileGraph(JSONUtil.parseObj(trim));
        }
        if (!JSONUtil.isTypeJSONArray(blocksJson)) {
            return "";
        }
        JSONArray blocks = JSONUtil.parseArray(blocksJson);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            appendBlock(sb, blocks.getJSONObject(i));
        }
        return sb.toString().trim();
    }

    private static String compileGraph(JSONObject graph) {
        JSONArray nodes = graph.getJSONArray("nodes");
        JSONArray edges = graph.getJSONArray("edges");
        if (nodes == null || nodes.isEmpty()) {
            return "";
        }
        Map<String, JSONObject> nodeMap = new LinkedHashMap<>();
        String startId = null;
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (node == null) {
                continue;
            }
            String id = node.getStr("id");
            if (StrUtil.isBlank(id)) {
                continue;
            }
            nodeMap.put(id, node);
            if (startId == null && "start".equals(nodeType(node))) {
                startId = id;
            }
        }
        if (StrUtil.isBlank(startId) && !nodeMap.isEmpty()) {
            startId = nodeMap.keySet().iterator().next();
        }
        Map<String, List<JSONObject>> outgoing = new HashMap<>();
        if (edges != null) {
            for (int i = 0; i < edges.size(); i++) {
                JSONObject edge = edges.getJSONObject(i);
                if (edge == null || StrUtil.isBlank(edge.getStr("source"))) {
                    continue;
                }
                outgoing.computeIfAbsent(edge.getStr("source"), key -> new ArrayList<>()).add(edge);
            }
        }
        StringBuilder sb = new StringBuilder();
        walk(startId, nodeMap, outgoing, new HashSet<>(), sb);
        return sb.toString().trim();
    }

    private static void walk(String id, Map<String, JSONObject> nodeMap, Map<String, List<JSONObject>> outgoing,
                             Set<String> visited, StringBuilder sb) {
        if (StrUtil.isBlank(id) || visited.contains(id) || !nodeMap.containsKey(id)) {
            return;
        }
        visited.add(id);
        JSONObject node = nodeMap.get(id);
        appendBlock(sb, nodeData(node));
        List<JSONObject> next = outgoing.getOrDefault(id, Collections.emptyList());
        next.sort(Comparator.comparing(item -> StrUtil.blankToDefault(item.getStr("sourceHandle"), "out")));
        boolean branch = next.size() > 1;
        for (JSONObject edge : next) {
            if (branch) {
                String label = StrUtil.blankToDefault(edge.getStr("label"), handleLabel(edge.getStr("sourceHandle")));
                if (sb.length() > 0 && !sb.toString().endsWith("\n")) {
                    sb.append("\n");
                }
                sb.append("分支（").append(label).append("）：\n");
            }
            walk(edge.getStr("target"), nodeMap, outgoing, visited, sb);
        }
    }

    private static JSONObject nodeData(JSONObject node) {
        JSONObject data = node.getJSONObject("data");
        if (data != null) {
            return data;
        }
        return node;
    }

    private static String nodeType(JSONObject node) {
        JSONObject data = node.getJSONObject("data");
        if (data != null) {
            return StrUtil.blankToDefault(data.getStr("nodeType"), data.getStr("type"));
        }
        return node.getStr("type");
    }

    private static String handleLabel(String handle) {
        if ("no".equals(handle)) {
            return "否";
        }
        if ("out".equals(handle) || "yes".equals(handle) || StrUtil.isBlank(handle)) {
            return "是";
        }
        return handle;
    }

    private static void appendBlock(StringBuilder sb, JSONObject block) {
        if (block == null) {
            return;
        }
        String type = StrUtil.blankToDefault(block.getStr("nodeType"), block.getStr("type"));
        if ("start".equals(type) || "end".equals(type)) {
            return;
        }
        String title = StrUtil.blankToDefault(block.getStr("title"),
            StrUtil.blankToDefault(block.getStr("label"), typeTitle(type)));
        String content = StrUtil.nullToEmpty(block.getStr("content")).trim();
        String menuId = StrUtil.nullToEmpty(block.getStr("menuId")).trim();
        String menuName = StrUtil.nullToEmpty(block.getStr("menuName")).trim();
        if (StrUtil.isBlank(title) && StrUtil.isBlank(content) && StrUtil.isBlank(menuId) && StrUtil.isBlank(menuName)) {
            return;
        }
        if (sb.length() > 0) {
            sb.append("\n");
        }
        if (StrUtil.isNotBlank(title)) {
            sb.append(title).append("：\n");
        }
        if (StrUtil.isNotBlank(content)) {
            sb.append(content).append("\n");
        }
        if ("navigate".equals(type) && (StrUtil.isNotBlank(menuId) || StrUtil.isNotBlank(menuName))) {
            String showName = StrUtil.blankToDefault(menuName, menuId);
            sb.append("绑定系统菜单「").append(showName).append("」");
            if (StrUtil.isNotBlank(menuId)) {
                sb.append("，menuId=").append(menuId);
            }
            sb.append("。用户明确要去办理时，actions 输出 ");
            sb.append("{\"type\":\"navigate\",\"menuId\":\"").append(jsonText(menuId))
                .append("\",\"menuName\":\"").append(jsonText(showName))
                .append("\",\"label\":\"打开").append(jsonText(showName)).append("\"}。");
            sb.append("只问说明、不办理时 actions 为空，不要编造其它菜单。\n");
        }
    }

    private static String typeTitle(String type) {
        if (StrUtil.isBlank(type)) {
            return "说明";
        }
        switch (type) {
            case "goal":
                return "目标";
            case "entry":
                return "入口";
            case "steps":
                return "办理步骤";
            case "fields":
                return "必填与字段";
            case "rules":
                return "校验与卡点";
            case "condition":
                return "条件分支";
            case "navigate":
                return "打开页面";
            case "notes":
                return "注意事项";
            default:
                return "说明";
        }
    }

    private static String jsonText(String raw) {
        return StrUtil.nullToEmpty(raw).replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
