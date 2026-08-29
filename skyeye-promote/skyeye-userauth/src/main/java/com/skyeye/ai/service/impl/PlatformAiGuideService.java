/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.util.PlatformAiChatHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 平台全量引导 AI 编排（提示词在后端组装，不落业务库）。
 */
@Service
public class PlatformAiGuideService {

    private static final String JSON_BLOCK_BEGIN = "@@SKYEYE_JSON_BEGIN@@";

    private static final String JSON_BLOCK_END = "@@SKYEYE_JSON_END@@";

    private static final String BIZ_TYPE_CHAT = "chat";

    private static final int MENU_PROMPT_LIMIT = 100;

    @Autowired
    private PlatformAiChatHelper platformAiChatHelper;

    public void generate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> bean = generate(inputObject.getParams());
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    public Map<String, Object> generate(Map<String, Object> params) {
        String question = params.get("question").toString().trim();
        String pageTitle = params.get("pageTitle").toString();
        String pagePath = params.get("pagePath").toString();
        List<Map<String, Object>> menus = parseMenus(params.get("menus"));
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("saveChat", 1);
        return platformAiChatHelper.startStreamingChat(
            buildUserContent(question, pageTitle, pagePath, menus),
            BIZ_TYPE_CHAT,
            extraParams);
    }

    private String buildUserContent(String question, String pageTitle, String pagePath,
                                    List<Map<String, Object>> menus) {
        List<Map<String, Object>> catalog = pickMenusForPrompt(menus, question);
        StringBuilder sb = new StringBuilder();
        sb.append("用户问题：").append(question).append("\n\n");
        sb.append("当前页面：").append(StrUtil.blankToDefault(pageTitle, "未知"));
        if (StrUtil.isNotBlank(pagePath)) {
            sb.append("（").append(pagePath).append("）");
        }
        sb.append("\n\n");
        sb.append("用户有权限访问的菜单（id | 路径，只能从这里选跳转目标）：\n");
        if (catalog.isEmpty()) {
            sb.append("（暂无菜单）\n");
        } else {
            for (int i = 0; i < catalog.size(); i++) {
                Map<String, Object> item = catalog.get(i);
                String id = firstText(item, "id", "key");
                String path = firstText(item, "routerPath", "name");
                sb.append(i + 1).append(". ").append(id).append(" | ").append(path).append("\n");
            }
        }
        sb.append("\n");
        sb.append("你是 SkyEye 云平台使用向导。根据当前页面和菜单回答如何操作。\n");
        sb.append("规则：\n");
        sb.append("1. 先用中文简洁说明，不要编造没有出现在菜单列表里的功能\n");
        sb.append("2. 需要跳转时给出 actions，menuId 必须是上面的 id\n");
        sb.append("3. 找不到对应菜单时 actions 为空数组\n");
        sb.append("4. 一次最多 3 个动作\n");
        appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"reply\": \"给用户看的说明\",\n"
                + "  \"actions\": [{\"type\": \"navigate\", \"menuId\": \"菜单id\", \"label\": \"打开xxx\"}]\n"
                + "}");
        return sb.toString();
    }

    private void appendMarkedJsonOutput(StringBuilder sb, String exampleJson) {
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

    private List<Map<String, Object>> pickMenusForPrompt(List<Map<String, Object>> menus, String question) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        if (menus.size() <= MENU_PROMPT_LIMIT) {
            return menus;
        }
        List<String> keywords = tokenize(question);
        if (keywords.isEmpty()) {
            return new ArrayList<>(menus.subList(0, MENU_PROMPT_LIMIT));
        }
        List<Map<String, Object>> scored = new ArrayList<>(menus);
        scored.sort(Comparator.comparingInt((Map<String, Object> item) -> scoreMenu(item, keywords)).reversed());
        List<Map<String, Object>> matched = new ArrayList<>();
        for (Map<String, Object> item : scored) {
            if (scoreMenu(item, keywords) <= 0) {
                break;
            }
            matched.add(item);
            if (matched.size() >= MENU_PROMPT_LIMIT) {
                break;
            }
        }
        if (matched.size() >= 12) {
            return matched;
        }
        return new ArrayList<>(menus.subList(0, MENU_PROMPT_LIMIT));
    }

    private int scoreMenu(Map<String, Object> item, List<String> keywords) {
        String hay = (firstText(item, "name") + " " + firstText(item, "routerPath")).toLowerCase();
        int score = 0;
        for (String word : keywords) {
            String needle = word.toLowerCase();
            if (hay.contains(needle)) {
                score += needle.length();
            }
        }
        return score;
    }

    private List<String> tokenize(String question) {
        List<String> result = new ArrayList<>();
        if (StrUtil.isBlank(question)) {
            return result;
        }
        String[] parts = question.split("[\\s,，。？?、/]+");
        for (String part : parts) {
            String word = part.trim();
            if (word.length() >= 2) {
                result.add(word);
            }
        }
        return result;
    }

    private List<Map<String, Object>> parseMenus(Object raw) {
        if (raw == null) {
            return Collections.emptyList();
        }
        if (raw instanceof List) {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object item : (List<?>) raw) {
                if (item instanceof Map) {
                    result.add(castMap(item));
                }
            }
            return result;
        }
        String text = raw.toString().trim();
        if (StrUtil.isBlank(text) || "[]".equals(text)) {
            return Collections.emptyList();
        }
        try {
            JSONArray array = JSONUtil.parseArray(text);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object item : array) {
                if (item == null) {
                    continue;
                }
                JSONObject json = JSONUtil.parseObj(item);
                result.add(new HashMap<>(json));
            }
            return result;
        } catch (Exception e) {
            throw new CustomException("菜单数据格式不正确");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object item) {
        return (Map<String, Object>) item;
    }

    private String firstText(Map<String, Object> item, String... keys) {
        if (item == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            Object value = item.get(key);
            if (value != null && StrUtil.isNotBlank(value.toString())) {
                return value.toString().trim();
            }
        }
        return "";
    }
}
