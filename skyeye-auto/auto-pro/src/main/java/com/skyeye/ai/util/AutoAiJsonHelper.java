/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.util;

import cn.hutool.core.util.StrUtil;

/**
 * 自动化模块 AI 专用工具（断言出参规范等）。
 * JSON 标记块公共能力请直接使用 {@link com.skyeye.common.util.AiJsonHelper}。
 */
public final class AutoAiJsonHelper {

    private AutoAiJsonHelper() {
    }

    /**
     * SkyEye 项目接口统一出参说明（写入 AI prompt）。
     */
    public static void appendSkyeyeApiResponseRules(StringBuilder sb) {
        sb.append("本项目接口出参一般为 SkyEye 标准格式：\n");
        sb.append("- returnCode：0 表示成功（不是 HTTP 200，也不是 code 字段）\n");
        sb.append("- returnMessage：提示信息，如「成功」\n");
        sb.append("- total：列表总条数\n");
        sb.append("- rows：分页/列表业务数据\n");
        sb.append("- bean 或 data：单对象业务数据\n");
        sb.append("断言 key 必须匹配输出中真实存在的路径；优先 returnCode、returnMessage，再断言 rows/bean/data 内关键字段。\n");
    }

    public static String[] normalizeSkyeyeAssertKeyValue(String key, String value) {
        if (StrUtil.isBlank(key)) {
            return new String[]{key, value};
        }
        String normalizedKey = key.trim();
        String normalizedValue = value == null ? "" : value.trim();
        if (normalizedKey.endsWith(".code")) {
            normalizedKey = normalizedKey.substring(0, normalizedKey.length() - 5) + ".returnCode";
            if ("200".equals(normalizedValue)) {
                normalizedValue = "0";
            }
        }
        return new String[]{normalizedKey, normalizedValue};
    }
}
