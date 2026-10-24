/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.tool;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.agent.AiAgentTool;
import com.skyeye.ai.agent.AiAgentToolContext;
import com.skyeye.ai.agent.boardconfig.AiBizBoardExecutor;
import com.skyeye.ai.agent.boardconfig.AiBizBoardRegistry;
import com.skyeye.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Agent 工具：统一业务看板笔数查询（只读）。
 * <p>
 * <b>为什么只有一个 Tool</b>：oa/erp/mall 取数方式虽不同，但对模型都是「给 domain，拿 counts」。
 * 差异全部放进配置（{@code ai-biz-board-endpoints.json} / 平台 bizBoardEndpoints），
 * 本类只做入参校验 + 调 {@link AiBizBoardExecutor} + 包装 JSON。
 * <p>
 * <b>模型怎么用</b>：
 * <ul>
 *   <li>必填 {@code domain}：oa / erp / mall</li>
 *   <li>可选 {@code codes}：只要部分指标，如 ["E01","pending"]</li>
 *   <li>可选 {@code storeId}：商城门店经营（配置了 storeVariant 时生效）</li>
 * </ul>
 * 返回 {@code counts}（数字）+ {@code labels}（中文名），禁止臆测数字。
 */
@Slf4j
@Component
public class BizBoardTool implements AiAgentTool {

    @Autowired
    private AiBizBoardExecutor executor;

    @Autowired
    private AiBizBoardRegistry registry;

    @Override
    public String name() {
        return "biz_board";
    }

    /**
     * 描述随配置动态生成，让模型知道当前有哪些 domain/code
     */
    @Override
    public String description() {
        return registry.buildToolDescription();
    }

    @Override
    public JSONObject parametersSchema() {
        Set<String> domains = registry.domains();
        String domainDesc = domains == null || domains.isEmpty()
            ? "业务域" : "业务域：" + String.join(" / ", domains);
        return JSONUtil.createObj()
            .set("type", "object")
            .set("properties", JSONUtil.createObj()
                .set("domain", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", domainDesc))
                .set("storeId", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", "可选。mall 且查门店经营时传入门店 id"))
                .set("codes", JSONUtil.createObj()
                    .set("type", "array")
                    .set("items", JSONUtil.createObj().set("type", "string"))
                    .set("description", "可选。只要部分指标，如 [\"E01\",\"pending\"]；不传则返回该域整板")))
            .set("required", new JSONArray().put("domain"));
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        String domain = args == null ? null : args.getStr("domain");
        String storeId = args == null ? null : args.getStr("storeId");
        JSONArray codes = args == null ? null : args.getJSONArray("codes");
        if (StrUtil.isBlank(domain)) {
            return JSONUtil.createObj().set("ok", false).set("error", "domain 必填").toString();
        }
        try {
            Map<String, Object> data = executor.execute(domain, storeId, codes);
            return JSONUtil.createObj()
                .set("ok", true)
                .set("domain", data.get("domain"))
                .set("scope", data.get("scope"))
                .set("hint", data.get("hint"))
                .set("counts", data.get("counts"))
                .set("labels", data.get("labels"))
                .toString();
        } catch (CustomException e) {
            log.error("biz_board failed domain={}, storeId={}, err={}", domain, storeId, e.getMessage(), e);
            JSONObject err = JSONUtil.createObj().set("ok", false).set("error", e.getMessage());
            if (registry.domains() != null && !registry.domains().isEmpty()) {
                err.set("supportedDomains", registry.domains());
            }
            return err.toString();
        } catch (Exception e) {
            log.error("biz_board failed domain={}, storeId={}", domain, storeId, e);
            return JSONUtil.createObj()
                .set("ok", false)
                .set("error", StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()))
                .toString();
        }
    }
}
