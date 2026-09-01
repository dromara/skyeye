/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.skill;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.ai.IAiSkillRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 办公 AI 用户消息：问题 + 当前页 + 命中的套件/技能。
 * 人设不在这里写，走绑定角色的 prompt（ChatService 的 systemPrompt）。
 */
@Component
public class PlatformAiSkillPromptBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformAiSkillPromptBuilder.class);

    private static final String JSON_BLOCK_BEGIN = "@@SKYEYE_JSON_BEGIN@@";

    private static final String JSON_BLOCK_END = "@@SKYEYE_JSON_END@@";

    @Autowired
    private IAiSkillRest iAiSkillRest;

    public String build(String question, String pageTitle, String pagePath) {
        MatchResult match = match(question, pageTitle, pagePath);
        StringBuilder sb = new StringBuilder();
        appendQuestionAndPage(sb, question, pageTitle, pagePath);
        if (match.suite != null) {
            LOGGER.info("platform ai suite matched, name={}", str(match.suite, "name"));
            appendSuite(sb, match);
        } else if (match.skill != null) {
            LOGGER.info("platform ai skill matched, name={}", str(match.skill, "name"));
            appendSkill(sb, match.skill);
        }
        appendMarkedJsonOutput(sb);
        return sb.toString();
    }

    private MatchResult match(String question, String pageTitle, String pagePath) {
        MatchResult result = new MatchResult();
        Map<String, Object> payload = loadMatchPayload();
        List<Map<String, Object>> skills = asMapList(payload.get("skillList"));
        List<Map<String, Object>> suites = asMapList(payload.get("suiteList"));
        if (CollectionUtil.isEmpty(skills) && CollectionUtil.isEmpty(suites)) {
            return result;
        }
        String haystack = (StrUtil.nullToEmpty(question) + " "
            + StrUtil.nullToEmpty(pageTitle) + " "
            + StrUtil.nullToEmpty(pagePath)).toLowerCase(Locale.ROOT);

        Map<String, Object> bestSkill = null;
        int bestSkillScore = 0;
        for (Map<String, Object> skill : skills) {
            int score = scoreItem(haystack, skill, pagePath);
            if (score > bestSkillScore) {
                bestSkillScore = score;
                bestSkill = skill;
            }
        }

        Map<String, Object> bestSuite = null;
        int bestSuiteScore = 0;
        for (Map<String, Object> suite : suites) {
            int score = scoreKeywords(haystack, str(suite, "keywords")) + scoreKeywords(haystack, str(suite, "name"));
            if (score > bestSuiteScore) {
                bestSuiteScore = score;
                bestSuite = suite;
            }
        }

        if (bestSkill != null && StrUtil.isNotBlank(str(bestSkill, "suiteId"))) {
            bestSuite = findById(suites, str(bestSkill, "suiteId"));
        }
        result.suite = bestSuite;
        result.skill = bestSkill;
        if (bestSuite != null) {
            result.suiteSkills = skillsOfSuite(skills, str(bestSuite, "id"), null);
        }
        return result;
    }

    private Map<String, Object> loadMatchPayload() {
        try {
            Object bean = ExecuteFeignClient.get(() -> iAiSkillRest.queryEnabledAiSkillMatchList(new HashMap<>())).getBean();
            if (bean instanceof Map) {
                return (Map<String, Object>) bean;
            }
        } catch (Exception e) {
            LOGGER.warn("load ai skill match list failed: {}", e.getMessage());
        }
        return Collections.emptyMap();
    }

    private int scoreItem(String haystack, Map<String, Object> skill, String pagePath) {
        int score = scoreKeywords(haystack, str(skill, "keywords"));
        score += scoreKeywords(haystack, str(skill, "name"));
        String className = str(skill, "serviceClassName");
        if (StrUtil.isNotBlank(className) && StrUtil.isNotBlank(pagePath)) {
            String simple = className;
            int dot = className.lastIndexOf('.');
            if (dot >= 0 && dot < className.length() - 1) {
                simple = className.substring(dot + 1);
            }
            if (pagePath.toLowerCase(Locale.ROOT).contains(simple.toLowerCase(Locale.ROOT))) {
                score += 8;
            }
        }
        return score;
    }

    private int scoreKeywords(String haystack, String keywords) {
        if (StrUtil.isBlank(keywords) || StrUtil.isBlank(haystack)) {
            return 0;
        }
        int score = 0;
        for (String raw : keywords.split("[,，;；]")) {
            String word = raw.trim().toLowerCase(Locale.ROOT);
            if (word.length() >= 2 && haystack.contains(word)) {
                score += Math.min(word.length(), 6);
            }
        }
        return score;
    }

    private void appendQuestionAndPage(StringBuilder sb, String question, String pageTitle, String pagePath) {
        sb.append("用户问题：").append(StrUtil.nullToEmpty(question).trim()).append("\n\n");
        sb.append("当前页面：").append(StrUtil.blankToDefault(pageTitle, "未知"));
        if (StrUtil.isNotBlank(pagePath)) {
            sb.append("（").append(pagePath).append("）");
        }
        sb.append("\n\n");
    }

    /**
     * 命中套件时把套件说明和套件内全部技能说明书拼上，由模型按用户问题选用。
     */
    private void appendSuite(StringBuilder sb, MatchResult match) {
        sb.append("【套件：").append(str(match.suite, "name")).append("】\n");
        if (StrUtil.isNotBlank(str(match.suite, "description"))) {
            sb.append(str(match.suite, "description")).append("\n");
        }
        sb.append("下面是该套件内的技能说明书，按用户实际问题选用对应技能，不要让用户切换技能。\n\n");
        if (CollectionUtil.isEmpty(match.suiteSkills)) {
            return;
        }
        for (Map<String, Object> skill : match.suiteSkills) {
            appendSkill(sb, skill);
        }
    }

    private void appendSkill(StringBuilder sb, Map<String, Object> skill) {
        sb.append("【技能：").append(str(skill, "name")).append("】\n");
        if (StrUtil.isNotBlank(str(skill, "description"))) {
            sb.append(str(skill, "description")).append("\n");
        }
        String instruction = str(skill, "instruction");
        if (StrUtil.isNotBlank(instruction)) {
            sb.append(instruction).append("\n");
        }
        sb.append("\n");
    }

    private void appendMarkedJsonOutput(StringBuilder sb) {
        sb.append("输出格式（必须严格遵守）：\n");
        sb.append("1. 可在 ").append(JSON_BLOCK_BEGIN).append(" 与 ").append(JSON_BLOCK_END).append(" 之外写思考或说明\n");
        sb.append("2. 两个标记之间只能有一个合法 JSON 对象，不要 markdown 代码块\n");
        sb.append("3. 程序只读取标记之间的内容\n");
        sb.append("示例：\n");
        sb.append(JSON_BLOCK_BEGIN).append("\n");
        sb.append("{\"reply\":\"给用户看的说明\",\"actions\":[]}\n");
        sb.append(JSON_BLOCK_END).append("\n");
    }

    private Map<String, Object> findById(List<Map<String, Object>> list, String id) {
        if (CollectionUtil.isEmpty(list) || StrUtil.isBlank(id)) {
            return null;
        }
        for (Map<String, Object> item : list) {
            if (id.equals(str(item, "id"))) {
                return item;
            }
        }
        return null;
    }

    private List<Map<String, Object>> skillsOfSuite(List<Map<String, Object>> skills, String suiteId, String excludeId) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(skills) || StrUtil.isBlank(suiteId)) {
            return result;
        }
        for (Map<String, Object> skill : skills) {
            if (!suiteId.equals(str(skill, "suiteId"))) {
                continue;
            }
            if (StrUtil.isNotBlank(excludeId) && excludeId.equals(str(skill, "id"))) {
                continue;
            }
            result.add(skill);
        }
        return result;
    }

    private List<Map<String, Object>> asMapList(Object value) {
        if (!(value instanceof List)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object item : (List<?>) value) {
            if (item instanceof Map) {
                list.add((Map<String, Object>) item);
            }
        }
        return list;
    }

    private String str(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return "";
        }
        return String.valueOf(map.get(key));
    }

    private static class MatchResult {
        private Map<String, Object> skill;
        private Map<String, Object> suite;
        private List<Map<String, Object>> suiteSkills;
    }
}
