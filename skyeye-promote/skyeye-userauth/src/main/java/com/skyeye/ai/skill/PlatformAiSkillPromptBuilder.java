/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.skill;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.util.AiJsonHelper;
import com.skyeye.rest.ai.IAiSkillRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 办公 AI 用户消息：问题 + 当前页 + 命中的套件/技能。
 * 人设不在这里写，走绑定角色的 prompt（ChatService 的 systemPrompt）。
 */
@Component
public class PlatformAiSkillPromptBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformAiSkillPromptBuilder.class);

    @Autowired
    private IAiSkillRest iAiSkillRest;

    public String build(String question, String pageTitle, String pagePath) {
        return build(question, pageTitle, pagePath, null, null);
    }

    public String build(String question, String pageTitle, String pagePath, String skillId, String suiteId) {
        Map<String, Object> payload = loadMatchPayload();
        List<Map<String, Object>> skills = asMapList(payload.get("skillList"));
        List<Map<String, Object>> suites = asMapList(payload.get("suiteList"));
        MatchResult match = match(question, pageTitle, pagePath, skillId, suiteId, skills, suites);
        StringBuilder sb = new StringBuilder();
        appendQuestionAndPage(sb, question, pageTitle, pagePath);
        appendMatched(sb, match, skills);
        appendMarkedJsonOutput(sb);
        return sb.toString();
    }

    /**
     * 表单布局 AI 辅助：注入技能说明书 + 表单字段定义与当前值。
     */
    public String buildForDsForm(String question, String pageTitle, String appId, String serviceClassName,
                                 String skillId, String suiteId, String formContextJson) {
        // 加载启用技能,套件,skill列表
        Map<String, Object> payload = loadMatchPayload();
        List<Map<String, Object>> skills = asMapList(payload.get("skillList"));
        List<Map<String, Object>> suites = asMapList(payload.get("suiteList"));
        String matchPath = StrUtil.blankToDefault(serviceClassName, pageTitle);
        MatchResult match = match(question, pageTitle, matchPath, skillId, suiteId, skills, suites);
        // 关键词/点选都未命中时，按 appId + 全路径 className 兜底
        if (match.skills.isEmpty() && match.suites.isEmpty()
            && StrUtil.isNotBlank(appId) && StrUtil.isNotBlank(serviceClassName)) {
            match = matchByServiceClassName(appId, serviceClassName, skills, suites);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("你是企业业务系统里的表单填写助手。用户正在使用「表单布局」页面编辑业务数据。\n");
        sb.append("请结合下方技能说明书、表单字段定义与当前值，理解用户指令后给出可落地的建议或草稿。\n\n");
        appendQuestionAndPage(sb, question, pageTitle, matchPath);
        appendMatched(sb, match, skills);
        appendFormContext(sb, formContextJson);
        appendDsFormJsonOutput(sb, formContextJson);
        return sb.toString();
    }

    private void appendMatched(StringBuilder sb, MatchResult match, List<Map<String, Object>> skills) {
        Set<String> writtenSkillIds = new HashSet<>();
        for (Map<String, Object> suite : match.suites) {
            LOGGER.info("platform ai suite matched, name={}", str(suite, "name"));
            MatchResult one = new MatchResult();
            one.suite = suite;
            one.suiteSkills = skillsOfSuite(skills, str(suite, "id"), null);
            appendSuite(sb, one);
            for (Map<String, Object> skill : one.suiteSkills) {
                writtenSkillIds.add(str(skill, "id"));
            }
        }
        for (Map<String, Object> skill : match.skills) {
            if (writtenSkillIds.contains(str(skill, "id"))) {
                continue;
            }
            LOGGER.info("platform ai skill matched, name={}", str(skill, "name"));
            appendSkill(sb, skill);
            writtenSkillIds.add(str(skill, "id"));
        }
    }

    private MatchResult match(String question, String pageTitle, String pagePath, String skillId, String suiteId,
                              List<Map<String, Object>> skills, List<Map<String, Object>> suites) {
        MatchResult result = new MatchResult();
        // 无可用技能/套件时直接返回空结果
        if (CollectionUtil.isEmpty(skills) && CollectionUtil.isEmpty(suites)) {
            return result;
        }
        // 用户点选的套件/技能 id（聊天可多选，逗号等分隔；表单布局一般只传一个）
        List<String> suiteIds = splitIds(suiteId);
        List<String> skillIds = splitIds(skillId);
        // 有点选时：按 id 精确命中，不再做关键词打分
        if (!suiteIds.isEmpty() || !skillIds.isEmpty()) {
            Map<String, Map<String, Object>> suiteMap = new LinkedHashMap<>();
            Map<String, Map<String, Object>> skillMap = new LinkedHashMap<>();
            // 先收点选的套件
            for (String id : suiteIds) {
                Map<String, Object> suite = findById(suites, id);
                if (suite != null) {
                    suiteMap.put(str(suite, "id"), suite);
                }
            }
            // 再收点选的技能；若技能属于某套件，则提升为该套件（避免只注入单技能）
            for (String id : skillIds) {
                Map<String, Object> skill = findById(skills, id);
                if (skill == null) {
                    continue;
                }
                String parentSuiteId = str(skill, "suiteId");
                if (StrUtil.isNotBlank(parentSuiteId)) {
                    Map<String, Object> suite = findById(suites, parentSuiteId);
                    if (suite != null) {
                        suiteMap.put(str(suite, "id"), suite);
                        continue;
                    }
                }
                // 独立技能（未挂套件）放入 skills
                skillMap.put(str(skill, "id"), skill);
            }
            result.suites.addAll(suiteMap.values());
            result.skills.addAll(skillMap.values());
            return result;
        }
        // 未点选时：用问题 + 页面标题 + 路径做关键词匹配
        String haystack = (StrUtil.nullToEmpty(question) + " "
            + StrUtil.nullToEmpty(pageTitle) + " "
            + StrUtil.nullToEmpty(pagePath)).toLowerCase(Locale.ROOT);

        // 遍历技能，取得分最高的一条
        Map<String, Object> bestSkill = null;
        int bestSkillScore = 0;
        for (Map<String, Object> skill : skills) {
            int score = scoreItem(haystack, skill, pagePath);
            if (score > bestSkillScore) {
                bestSkillScore = score;
                bestSkill = skill;
            }
        }

        // 遍历套件，按名称/关键词取得分最高的一条
        Map<String, Object> bestSuite = null;
        int bestSuiteScore = 0;
        for (Map<String, Object> suite : suites) {
            int score = scoreKeywords(haystack, str(suite, "keywords")) + scoreKeywords(haystack, str(suite, "name"));
            if (score > bestSuiteScore) {
                bestSuiteScore = score;
                bestSuite = suite;
            }
        }

        // 最高分技能若挂了套件，优先用该套件覆盖（注入套件内全部技能）
        if (bestSkill != null && StrUtil.isNotBlank(str(bestSkill, "suiteId"))) {
            bestSuite = findById(suites, str(bestSkill, "suiteId"));
        }
        // 套件优先于单技能；都没有则返回空
        if (bestSuite != null) {
            result.suites.add(bestSuite);
        } else if (bestSkill != null) {
            result.skills.add(bestSkill);
        }
        return result;
    }

    private List<String> splitIds(String raw) {
        if (StrUtil.isBlank(raw)) {
            return Collections.emptyList();
        }
        List<String> ids = new ArrayList<>();
        for (String part : raw.split("[,，;；\\s]+")) {
            if (StrUtil.isNotBlank(part)) {
                ids.add(part.trim());
            }
        }
        return ids;
    }

    /**
     * 加载启用技能,套件,skill列表
     *
     * @return 加载启用技能, 套件, skill列表
     */
    private Map<String, Object> loadMatchPayload() {
        try {
            return ExecuteFeignClient.get(() -> iAiSkillRest.queryEnabledAiSkillMatchList(new HashMap<>())).getBean();
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
            String simple = simpleClassName(className);
            String hay = pagePath.toLowerCase(Locale.ROOT);
            if (hay.contains(simple.toLowerCase(Locale.ROOT))
                || hay.contains(className.toLowerCase(Locale.ROOT))) {
                score += 8;
            }
        }
        return score;
    }

    private MatchResult matchByServiceClassName(String appId, String serviceClassName,
                                                List<Map<String, Object>> skills,
                                                List<Map<String, Object>> suites) {
        MatchResult result = new MatchResult();
        // appId + 全路径 className 缺一不可（微服务下简单类名可能重复）
        if (StrUtil.isBlank(appId) || StrUtil.isBlank(serviceClassName)) {
            return result;
        }
        Map<String, Object> bestSkill = null;
        // 在启用技能里找 appId 与全路径 serviceClassName 都一致的第一条
        for (Map<String, Object> skill : skills) {
            if (!appId.equals(str(skill, "appId"))) {
                continue;
            }
            String skillClass = str(skill, "serviceClassName");
            if (StrUtil.isBlank(skillClass)) {
                continue;
            }
            // 必须全路径相等，不用简单类名兜底
            if (serviceClassName.equals(skillClass)) {
                bestSkill = skill;
                break;
            }
        }
        // 技能挂在套件下时，优先返回套件（后续会注入套件内全部技能说明书）
        if (bestSkill != null && StrUtil.isNotBlank(str(bestSkill, "suiteId"))) {
            Map<String, Object> suite = findById(suites, str(bestSkill, "suiteId"));
            if (suite != null) {
                result.suites.add(suite);
                return result;
            }
        }
        // 无套件或套件不存在时，只返回命中的单技能
        if (bestSkill != null) {
            result.skills.add(bestSkill);
        }
        return result;
    }

    private String simpleClassName(String className) {
        if (StrUtil.isBlank(className)) {
            return "";
        }
        int dot = className.lastIndexOf('.');
        if (dot >= 0 && dot < className.length() - 1) {
            return className.substring(dot + 1);
        }
        return className;
    }

    private void appendFormContext(StringBuilder sb, String formContextJson) {
        sb.append("【表单布局上下文】\n");
        if (StrUtil.isBlank(formContextJson)) {
            sb.append("（无）\n\n");
            return;
        }
        try {
            JSONObject ctx = JSONUtil.parseObj(formContextJson);
            if (StrUtil.isNotBlank(ctx.getStr("pageName"))) {
                sb.append("页面：").append(ctx.getStr("pageName")).append("\n");
            }
            if (StrUtil.isNotBlank(ctx.getStr("pageType"))) {
                sb.append("模式：").append(ctx.getStr("pageType")).append("\n");
            }
            if (StrUtil.isNotBlank(ctx.getStr("className"))) {
                sb.append("业务对象：").append(ctx.getStr("className")).append("\n");
            }
            if (StrUtil.isNotBlank(ctx.getStr("objectId"))) {
                sb.append("业务实例ID：").append(ctx.getStr("objectId")).append("\n");
            }
            JSONArray fields = ctx.getJSONArray("fields");
            if (fields == null || fields.isEmpty()) {
                sb.append("字段：（无）\n\n");
                return;
            }
            sb.append("字段清单（attrKey 为程序回填键，请只修改用户明确要求的字段）：\n");
            for (Object item : fields) {
                if (!(item instanceof JSONObject)) {
                    continue;
                }
                JSONObject field = (JSONObject) item;
                sb.append("- ").append(field.getStr("attrKey"));
                if (StrUtil.isNotBlank(field.getStr("title"))) {
                    sb.append("（").append(field.getStr("title")).append("）");
                }
                sb.append(" [").append(StrUtil.blankToDefault(field.getStr("numCode"), "unknown")).append("]");
                if (Boolean.TRUE.equals(field.getBool("required"))) {
                    sb.append(" *必填");
                }
                if (StrUtil.isNotBlank(field.getStr("remark"))) {
                    sb.append(" 说明:").append(field.getStr("remark").replace("\n", " "));
                }
                if (field.containsKey("columns") && field.get("columns") instanceof JSONArray) {
                    JSONArray columns = field.getJSONArray("columns");
                    if (!columns.isEmpty()) {
                        sb.append(" 列:");
                        for (Object col : columns) {
                            if (col instanceof JSONObject) {
                                JSONObject colObj = (JSONObject) col;
                                sb.append(colObj.getStr("title")).append("(")
                                    .append(colObj.getStr("attrKey")).append(") ");
                            }
                        }
                    }
                }
                sb.append("\n");
                if (field.containsKey("valueSummary")) {
                    sb.append("  当前值：").append(AiJsonHelper.normalizeJsonText(field.get("valueSummary"))).append("\n");
                } else if (field.containsKey("value")) {
                    sb.append("  当前值：").append(AiJsonHelper.normalizeJsonText(field.get("value"))).append("\n");
                }
            }
            sb.append("\n");
        } catch (Exception e) {
            sb.append(formContextJson).append("\n\n");
        }
    }

    private void appendDsFormJsonOutput(StringBuilder sb, String formContextJson) {
        StringBuilder exampleFields = new StringBuilder();
        exampleFields.append("    \"字段attrKey\": \"建议值或留空表示不改\"");
        try {
            JSONObject ctx = JSONUtil.parseObj(formContextJson);
            JSONArray fields = ctx.getJSONArray("fields");
            if (fields != null && !fields.isEmpty()) {
                exampleFields = new StringBuilder();
                int count = 0;
                for (Object item : fields) {
                    if (!(item instanceof JSONObject) || count >= 5) {
                        continue;
                    }
                    JSONObject field = (JSONObject) item;
                    if (StrUtil.isBlank(field.getStr("attrKey"))) {
                        continue;
                    }
                    if (exampleFields.length() > 0) {
                        exampleFields.append(",\n");
                    }
                    exampleFields.append("    \"").append(field.getStr("attrKey")).append("\": \"…\"");
                    count++;
                }
            }
        } catch (Exception ignored) {
            // use default example
        }
        AiJsonHelper.appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"reply\": \"给用户看的说明，解释你做了什么、还需要用户确认什么\",\n"
                + "  \"fieldValues\": {\n"
                + exampleFields + "\n"
                + "  },\n"
                + "  \"actions\": []\n"
                + "}");
        sb.append("要求：\n");
        sb.append("1. fieldValues 的 key 必须来自上方字段 attrKey，不要自造字段名\n");
        sb.append("2. 用户只是咨询时 fieldValues 可为空对象；需要帮填时才给出建议值\n");
        sb.append("3. 子表/清单类字段（simpleTable、bomChildList 等）value 用 JSON 数组字符串\n");
        sb.append("4. 富文本字段用 HTML；枚举/下拉必须从字段可选值里选原文\n");
        sb.append("5. actions 留空，除非技能说明书明确要求导航\n");
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
        AiJsonHelper.appendMarkedJsonOutput(sb, "{\"reply\":\"给用户看的说明\",\"actions\":[]}");
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
        private Map<String, Object> suite;
        private List<Map<String, Object>> suiteSkills = new ArrayList<>();
        private List<Map<String, Object>> suites = new ArrayList<>();
        private List<Map<String, Object>> skills = new ArrayList<>();
    }
}
