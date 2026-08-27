/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.demand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.ai.util.AutoAiChatHelper;
import com.skyeye.ai.util.AutoAiHtmlHelper;
import com.skyeye.ai.util.AutoAiJsonHelper;
import com.skyeye.ai.util.AutoAiProjectContextHelper;
import com.skyeye.api.dao.AutoApiDao;
import com.skyeye.api.entity.AutoApi;
import com.skyeye.attr.classenum.AttrSymbols;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.demand.entity.AutoDemand;
import com.skyeye.demand.service.AutoDemandService;
import com.skyeye.exception.CustomException;
import com.skyeye.usercase.classenum.AutoStepTypeEnum;
import com.skyeye.usercase.classenum.AutoValueFromTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 根据需求生成冒烟用例草稿（不落库）。
 */
@Service
public class AutoDemandCaseAiDraftService {

    private static final int MAX_STEP_COUNT = 12;

    private static final int MAX_API_CATALOG = 80;

    private static final Set<String> VALID_OPERATORS = new HashSet<>();

    static {
        for (AttrSymbols symbol : AttrSymbols.values()) {
            VALID_OPERATORS.add(symbol.getKey());
        }
    }

    @Autowired
    private AutoAiChatHelper autoAiChatHelper;

    @Autowired
    private AutoAiProjectContextHelper autoAiProjectContextHelper;

    @Autowired
    private AutoDemandService autoDemandService;

    @Autowired
    private AutoApiDao autoApiDao;

    public Map<String, Object> generate(Map<String, Object> params) {
        AutoDemand demand = loadDemand(params);
        List<AutoApi> apiList = loadApiCatalog(demand.getObjectId(), demand.getModuleId());
        if (CollectionUtil.isEmpty(apiList)) {
            throw new CustomException("当前模块下暂无接口，请先在接口管理中维护 API");
        }
        return autoAiChatHelper.startStreamingChat(
            buildUserContent(demand, apiList),
            "caseDraft");
    }

    public Map<String, Object> parseAnswer(Map<String, Object> params) {
        String demandId = params.get("demandId") == null ? "" : params.get("demandId").toString();
        AutoDemand demand = StrUtil.isBlank(demandId) ? null : autoDemandService.selectById(demandId);
        List<AutoApi> apiList = demand == null
            ? new ArrayList<>()
            : loadApiCatalog(demand.getObjectId(), demand.getModuleId());
        return parseDraft(autoAiChatHelper.requireAnswer(params), demand, apiList);
    }

    private AutoDemand loadDemand(Map<String, Object> params) {
        if (params.get("demandId") == null || StrUtil.isBlank(params.get("demandId").toString())) {
            throw new CustomException("需求id不能为空");
        }
        AutoDemand demand = autoDemandService.selectById(params.get("demandId").toString());
        if (demand == null) {
            throw new CustomException("需求不存在");
        }
        return demand;
    }

    private List<AutoApi> loadApiCatalog(String objectId, String moduleId) {
        QueryWrapper<AutoApi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoApi::getObjectId), objectId);
        if (StrUtil.isNotBlank(moduleId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoApi::getModuleId), moduleId);
        }
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AutoApi::getName));
        queryWrapper.last("limit " + MAX_API_CATALOG);
        return autoApiDao.selectList(queryWrapper);
    }

    private String buildUserContent(AutoDemand demand, List<AutoApi> apiList) {
        String moduleName = autoAiProjectContextHelper.loadModuleName(demand.getModuleId());
        String versionName = autoAiProjectContextHelper.loadVersionName(demand.getVersionId());
        StringBuilder sb = new StringBuilder();
        sb.append("你是自动化测试工程师，只输出 JSON，不要 markdown 代码块。\n");
        sb.append("请根据需求生成「冒烟测试用例」草稿：覆盖主流程关键接口，步骤简洁可执行，优先 3~8 个 API 步骤。\n");
        sb.append("需求编号：").append(StrUtil.blankToDefault(demand.getNo(), "无")).append("\n");
        sb.append("需求标题：").append(StrUtil.blankToDefault(demand.getName(), "无")).append("\n");
        sb.append("模块：").append(AutoAiHtmlHelper.nvlText(moduleName)).append("\n");
        sb.append("版本：").append(AutoAiHtmlHelper.nvlText(versionName)).append("\n");
        sb.append("需求内容：\n").append(AutoAiHtmlHelper.plainText(StrUtil.blankToDefault(demand.getContent(), ""))).append("\n");
        sb.append("备注：").append(AutoAiHtmlHelper.nvlText(demand.getRemark())).append("\n");
        sb.append("可选接口目录（stepApi.apiId 必须从中选取真实 id，不要编造）：\n");
        sb.append(buildApiCatalogJson(apiList)).append("\n");
        sb.append("规则：\n");
        sb.append("1. 仅生成 type=1 的 API 步骤，不要嵌套用例/数据库/脚本步骤\n");
        sb.append("2. 每个步骤必须有 stepApi.apiId，且来自上面目录\n");
        sb.append("3. stepInputList 填常用入参示例，valueFrom：1=自定义；2=表达式\n");
        sb.append("4. stepAssertList 的 key 使用占位符 {resultKey}.字段，如 {resultKey}.code\n");
        sb.append("5. operator 仅可取：equalTo、notEqual、lessThan、greaterThan、lessThanOrEqual、greaterThanOrEqual、contain\n");
        sb.append("6. 用例名称建议以 [冒烟] 开头\n");
        sb.append("请输出 JSON：\n");
        sb.append("{\n");
        sb.append("  \"name\": \"[冒烟] 用例名称\",\n");
        sb.append("  \"remark\": \"来自需求 xxx\",\n");
        sb.append("  \"stepList\": [\n");
        sb.append("    {\n");
        sb.append("      \"name\": \"步骤名\",\n");
        sb.append("      \"type\": 1,\n");
        sb.append("      \"orderBy\": 1,\n");
        sb.append("      \"stepApi\": {\"apiId\": \"目录中的id\"},\n");
        sb.append("      \"stepInputList\": [{\"key\": \"username\", \"valueFrom\": 1, \"value\": \"admin\"}],\n");
        sb.append("      \"stepAssertList\": [{\"key\": \"{resultKey}.code\", \"operator\": \"equalTo\", \"valueFrom\": 1, \"value\": \"200\", \"orderBy\": 1}]\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String buildApiCatalogJson(List<AutoApi> apiList) {
        JSONArray array = new JSONArray();
        for (AutoApi api : apiList) {
            JSONObject item = new JSONObject();
            item.set("id", api.getId());
            item.set("name", api.getName());
            item.set("requestWay", api.getRequestWay());
            item.set("address", api.getAddress());
            array.add(item);
        }
        return array.toString();
    }

    private Map<String, Object> parseDraft(String answer, AutoDemand demand, List<AutoApi> apiList) {
        JSONObject json = AutoAiJsonHelper.parseJsonObject(AutoAiJsonHelper.extractJson(answer));
        if (json == null) {
            throw new CustomException("未能解析出有效用例草稿，请重试");
        }
        Map<String, AutoApi> catalogById = new LinkedHashMap<>();
        Map<String, AutoApi> catalogByName = new LinkedHashMap<>();
        for (AutoApi api : apiList) {
            catalogById.put(api.getId(), api);
            if (StrUtil.isNotBlank(api.getName())) {
                catalogByName.put(api.getName().trim(), api);
            }
        }
        JSONArray stepArray = json.getJSONArray("stepList");
        List<Map<String, Object>> stepList = new ArrayList<>();
        if (stepArray != null) {
            for (Object item : stepArray) {
                Map<String, Object> row = normalizeStep(JSONUtil.parseObj(item), catalogById, catalogByName);
                if (row != null) {
                    stepList.add(row);
                }
                if (stepList.size() >= MAX_STEP_COUNT) {
                    break;
                }
            }
        }
        if (stepList.isEmpty()) {
            throw new CustomException("未能解析出有效步骤，请确认模块下接口与需求匹配后重试");
        }
        Map<String, Object> bean = new HashMap<>();
        String name = json.getStr("name");
        if (StrUtil.isBlank(name) && demand != null) {
            name = "[冒烟] " + StrUtil.blankToDefault(demand.getName(), "用例");
        }
        bean.put("name", StrUtil.blankToDefault(name, "[冒烟] 用例"));
        String remark = json.getStr("remark");
        if (StrUtil.isBlank(remark) && demand != null) {
            remark = "来自需求 " + StrUtil.blankToDefault(demand.getNo(), demand.getId());
        }
        bean.put("remark", StrUtil.blankToDefault(remark, ""));
        if (demand != null) {
            bean.put("moduleId", demand.getModuleId());
            bean.put("demandId", demand.getId());
        }
        bean.put("stepList", stepList);
        return bean;
    }

    private Map<String, Object> normalizeStep(JSONObject item, Map<String, AutoApi> catalogById,
                                              Map<String, AutoApi> catalogByName) {
        if (item == null) {
            return null;
        }
        Integer type = parseStepType(item.get("type"));
        if (type != AutoStepTypeEnum.STEP.getKey()) {
            return null;
        }
        JSONObject stepApiJson = item.getJSONObject("stepApi");
        String apiId = stepApiJson == null ? "" : StrUtil.blankToDefault(stepApiJson.getStr("apiId"), "");
        String apiName = stepApiJson == null ? "" : StrUtil.blankToDefault(stepApiJson.getStr("apiName"), "");
        apiId = resolveApiId(apiId, apiName, catalogById, catalogByName);
        if (StrUtil.isBlank(apiId)) {
            return null;
        }
        Map<String, Object> row = new HashMap<>();
        row.put("name", StrUtil.blankToDefault(item.getStr("name"), "API步骤"));
        row.put("type", type);
        row.put("orderBy", parseOrderBy(item.get("orderBy")));
        Map<String, Object> stepApi = new HashMap<>();
        stepApi.put("apiId", apiId);
        row.put("stepApi", stepApi);
        row.put("stepInputList", normalizeInputList(item.getJSONArray("stepInputList")));
        row.put("stepAssertList", normalizeAssertList(item.getJSONArray("stepAssertList")));
        return row;
    }

    private String resolveApiId(String apiId, String apiName, Map<String, AutoApi> catalogById,
                                Map<String, AutoApi> catalogByName) {
        if (StrUtil.isNotBlank(apiId) && catalogById.containsKey(apiId)) {
            return apiId;
        }
        if (StrUtil.isNotBlank(apiName) && catalogByName.containsKey(apiName.trim())) {
            return catalogByName.get(apiName.trim()).getId();
        }
        return "";
    }

    private Integer parseStepType(Object value) {
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return AutoStepTypeEnum.STEP.getKey();
        }
    }

    private int parseOrderBy(Object value) {
        try {
            int order = Integer.parseInt(String.valueOf(value));
            return Math.max(order, 1);
        } catch (Exception e) {
            return 1;
        }
    }

    private List<Map<String, Object>> normalizeInputList(JSONArray array) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (array == null) {
            return list;
        }
        for (Object item : array) {
            if (item == null) {
                continue;
            }
            JSONObject json = JSONUtil.parseObj(item);
            String key = json.getStr("key");
            if (StrUtil.isBlank(key)) {
                continue;
            }
            Map<String, Object> row = new HashMap<>();
            row.put("key", key.trim());
            row.put("valueFrom", parseValueFrom(json.get("valueFrom")));
            row.put("value", json.get("value") == null ? "" : String.valueOf(json.get("value")));
            list.add(row);
        }
        return list;
    }

    private List<Map<String, Object>> normalizeAssertList(JSONArray array) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (array == null) {
            return list;
        }
        int order = 1;
        for (Object item : array) {
            if (item == null) {
                continue;
            }
            JSONObject json = JSONUtil.parseObj(item);
            String key = json.getStr("key");
            if (StrUtil.isBlank(key)) {
                continue;
            }
            String operator = json.getStr("operator");
            if (StrUtil.isBlank(operator) || !VALID_OPERATORS.contains(operator)) {
                operator = AttrSymbols.EQUAL_TO.getKey();
            }
            Map<String, Object> row = new HashMap<>();
            row.put("key", key.trim());
            row.put("operator", operator);
            row.put("valueFrom", parseValueFrom(json.get("valueFrom")));
            row.put("value", json.get("value") == null ? "" : String.valueOf(json.get("value")));
            row.put("orderBy", parseOrderBy(json.get("orderBy") == null ? order : json.get("orderBy")));
            list.add(row);
            order++;
        }
        return list;
    }

    private Integer parseValueFrom(Object value) {
        if (value == null) {
            return AutoValueFromTypeEnum.CUSTOMIZE.getKey();
        }
        try {
            int from = Integer.parseInt(value.toString());
            if (from == AutoValueFromTypeEnum.EXPRESSION.getKey()) {
                return AutoValueFromTypeEnum.EXPRESSION.getKey();
            }
        } catch (Exception ignored) {
        }
        return AutoValueFromTypeEnum.CUSTOMIZE.getKey();
    }
}
