/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 把界面积木编译成模型可读的说明书。
 */
public final class AiSkillBlockCompiler {

    private AiSkillBlockCompiler() {
    }

    public static String compile(String blocksJson) {
        if (StrUtil.isBlank(blocksJson) || !JSONUtil.isTypeJSONArray(blocksJson)) {
            return "";
        }
        JSONArray blocks = JSONUtil.parseArray(blocksJson);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            JSONObject block = blocks.getJSONObject(i);
            if (block == null) {
                continue;
            }
            String title = StrUtil.blankToDefault(block.getStr("title"), typeTitle(block.getStr("type")));
            String content = StrUtil.nullToEmpty(block.getStr("content")).trim();
            if (StrUtil.isBlank(title) && StrUtil.isBlank(content)) {
                continue;
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
        }
        return sb.toString().trim();
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
            case "navigate":
                return "打开页面";
            case "notes":
                return "注意事项";
            default:
                return "说明";
        }
    }
}
