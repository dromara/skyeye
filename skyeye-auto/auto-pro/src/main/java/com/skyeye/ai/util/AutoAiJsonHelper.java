/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * AI 返回 JSON 解析工具。
 */
public final class AutoAiJsonHelper {

    private AutoAiJsonHelper() {
    }

    public static String extractJson(String answer) {
        if (StrUtil.isBlank(answer)) {
            return "";
        }
        String text = answer.trim();
        if (text.startsWith("```")) {
            int firstNl = text.indexOf('\n');
            if (firstNl > 0) {
                text = text.substring(firstNl + 1);
            }
            int lastFence = text.lastIndexOf("```");
            if (lastFence >= 0) {
                text = text.substring(0, lastFence);
            }
            text = text.trim();
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
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
