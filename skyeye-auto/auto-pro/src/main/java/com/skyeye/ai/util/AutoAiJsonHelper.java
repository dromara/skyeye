/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * AI 返回 JSON 解析工具。
 */
public final class AutoAiJsonHelper {

    /** AI 结构化输出起始标记（标记之间为可被程序解析的 JSON）。 */
    public static final String JSON_BLOCK_BEGIN = "@@SKYEYE_JSON_BEGIN@@";

    /** AI 结构化输出结束标记。 */
    public static final String JSON_BLOCK_END = "@@SKYEYE_JSON_END@@";

    private AutoAiJsonHelper() {
    }

    /**
     * 追加「标记包裹 JSON」输出规范与示例（标记外可思考，标记内仅 JSON）。
     */
    public static void appendMarkedJsonOutput(StringBuilder sb, String exampleJson) {
        sb.append("输出格式（必须严格遵守）：\n");
        sb.append("1. 可在 ").append(JSON_BLOCK_BEGIN).append(" 与 ")
            .append(JSON_BLOCK_END).append(" 之外写思考或说明\n");
        sb.append("2. 两个标记之间只能有一个合法 JSON 对象或数组，不要 markdown 代码块\n");
        sb.append("3. 程序只读取标记之间的内容，请务必输出完整起止标记\n");
        sb.append("示例：\n");
        sb.append(JSON_BLOCK_BEGIN).append("\n");
        sb.append(exampleJson.trim()).append("\n");
        sb.append(JSON_BLOCK_END).append("\n");
    }

    /**
     * 从 AI 回答中提取 JSON 文本：优先标记块；无标记时去掉 markdown 后取首段对象或数组（兼容旧回答）。
     */
    public static String extractJsonBlock(String answer) {
        if (StrUtil.isBlank(answer)) {
            return "";
        }
        String marked = extractMarkedJsonBlock(answer);
        if (StrUtil.isNotBlank(marked)) {
            return marked;
        }
        String text = stripMarkdownFence(answer.trim());
        int objStart = text.indexOf('{');
        int arrStart = text.indexOf('[');
        if (objStart >= 0 && (arrStart < 0 || objStart <= arrStart)) {
            int end = text.lastIndexOf('}');
            if (end > objStart) {
                return text.substring(objStart, end + 1);
            }
        }
        if (arrStart >= 0) {
            int end = text.lastIndexOf(']');
            if (end > arrStart) {
                return text.substring(arrStart, end + 1);
            }
        }
        return text;
    }

    /**
     * 从 AI 回答中提取标记之间的 JSON 文本；多次出现时取最后一组。
     */
    public static String extractMarkedJsonBlock(String answer) {
        if (StrUtil.isBlank(answer)) {
            return "";
        }
        int searchFrom = 0;
        String lastBlock = "";
        while (true) {
            int begin = answer.indexOf(JSON_BLOCK_BEGIN, searchFrom);
            if (begin < 0) {
                break;
            }
            int contentStart = begin + JSON_BLOCK_BEGIN.length();
            int end = answer.indexOf(JSON_BLOCK_END, contentStart);
            if (end < 0) {
                break;
            }
            lastBlock = answer.substring(contentStart, end).trim();
            searchFrom = end + JSON_BLOCK_END.length();
        }
        if (StrUtil.isNotBlank(lastBlock)) {
            return stripMarkdownFence(lastBlock);
        }
        return "";
    }

    private static String stripMarkdownFence(String text) {
        if (!text.startsWith("```")) {
            return text;
        }
        int firstNl = text.indexOf('\n');
        if (firstNl > 0) {
            text = text.substring(firstNl + 1);
        }
        int lastFence = text.lastIndexOf("```");
        if (lastFence >= 0) {
            text = text.substring(0, lastFence);
        }
        return text.trim();
    }

    public static JSONArray parseJsonArray(String jsonText) {
        if (StrUtil.isBlank(jsonText)) {
            return null;
        }
        try {
            return JSONUtil.parseArray(jsonText);
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject parseJsonObject(String jsonText) {
        if (StrUtil.isBlank(jsonText)) {
            return null;
        }
        try {
            return JSONUtil.parseObj(jsonText);
        } catch (Exception e) {
            return null;
        }
    }

    public static String normalizeJsonText(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            String str = value.toString().trim();
            if (StrUtil.isBlank(str)) {
                return "null";
            }
            if (JSONUtil.isTypeJSON(str)) {
                return str;
            }
            return JSONUtil.toJsonStr(str);
        }
        return JSONUtil.toJsonStr(value);
    }
}
