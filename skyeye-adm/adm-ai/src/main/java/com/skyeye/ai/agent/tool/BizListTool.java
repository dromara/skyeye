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
import com.skyeye.ai.agent.listconfig.AiBizListEndpointRegistry;
import com.skyeye.ai.agent.listconfig.AiBizListExecutor;
import com.skyeye.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Agent 工具：业务列表明细（只读），配置驱动。
 * <p>
 * <b>和 biz_board 的分工</b>：board 只给「笔数 counts」；本工具给「分页 items」供前端 list 卡片。
 * <p>
 * <b>入参</b>：domain（业务域，非微服务名）+ type（列表类型）+ 可选 storeId/page/limit。
 * 命中哪条配置、打哪个接口，见 {@code ai-biz-list-endpoints.json} / 平台 bizListEndpoints。
 * <p>
 * <b>路由</b>：springApplicationName 或 appId+serviceClassName → {@link com.skyeye.ai.agent.listconfig.AiBizListRemoteInvoker}。
 */
@Slf4j
@Component
public class BizListTool implements AiAgentTool {

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;

    @Autowired
    private AiBizListExecutor executor;

    @Autowired
    private AiBizListEndpointRegistry registry;

    @Override
    public String name() {
        return "biz_list";
    }

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
                    // 逻辑域，非 Feign/webroot 服务名
                    .set("description", domainDesc))
                .set("type", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", registry.buildTypeSchemaDescription()))
                .set("storeId", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", "可选。mall 且查门店售出时传入门店 id"))
                .set("page", JSONUtil.createObj()
                    .set("type", "integer")
                    .set("description", "页码，从 1 开始，默认 1"))
                .set("limit", JSONUtil.createObj()
                    .set("type", "integer")
                    .set("description", "每页条数，默认 5，最大 10")))
            .set("required", new JSONArray().put("domain").put("type"));
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        String domain = args == null ? null : args.getStr("domain");
        String type = args == null ? null : args.getStr("type");
        String storeId = args == null ? null : args.getStr("storeId");
        int page = args == null ? 1 : args.getInt("page", 1);
        int limit = args == null ? DEFAULT_LIMIT : args.getInt("limit", DEFAULT_LIMIT);
        if (page < 1) {
            page = 1;
        }
        if (limit < 1) {
            limit = DEFAULT_LIMIT;
        }
        if (limit > MAX_LIMIT) {
            limit = MAX_LIMIT;
        }
        if (StrUtil.isBlank(domain) || StrUtil.isBlank(type)) {
            return JSONUtil.createObj().set("ok", false).set("error", "domain 与 type 必填").toString();
        }
        try {
            Map<String, Object> data = executor.execute(domain, type, storeId, page, limit);
            JSONObject out = JSONUtil.createObj()
                .set("ok", true)
                .set("domain", data.get("domain"))
                .set("type", data.get("type"))
                .set("scope", data.get("scope"))
                .set("total", data.get("total"))
                .set("page", data.get("page"))
                .set("limit", data.get("limit"))
                .set("listTitle", data.get("listTitle"))
                .set("items", data.get("items"))
                .set("hint", "请在最终回复的 JSON 中设置 list:{title,items}，items 原样或精简引用本结果；reply 中用一两句话概括");
            if (data.get("storeId") != null) {
                out.set("storeId", data.get("storeId"));
            }
            return out.toString();
        } catch (CustomException e) {
            log.error("biz_list failed domain={}, type={}, err={}", domain, type, e.getMessage(), e);
            JSONObject err = JSONUtil.createObj().set("ok", false).set("error", e.getMessage());
            Map<String, List<String>> supported = registry.supported();
            if (supported != null && !supported.isEmpty()) {
                err.set("supported", supported);
            }
            return err.toString();
        } catch (Exception e) {
            log.error("biz_list failed domain={}, type={}", domain, type, e);
            return JSONUtil.createObj()
                .set("ok", false)
                .set("error", StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()))
                .toString();
        }
    }
}
