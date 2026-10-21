/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.tool;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.agent.AiAgentTool;
import com.skyeye.ai.agent.AiAgentToolContext;
import org.springframework.stereotype.Component;

/**
 * 业务数目查询占位：按 domain+metric 分发；C1 再接 ERP/OA/商城 Feign。
 */
@Component
public class BizCountGenericTool implements AiAgentTool {

    @Override
    public String name() {
        return "biz_count_generic";
    }

    @Override
    public String description() {
        return "查询业务数目/统计（待出库、待办、今日订单等）。"
            + "涉及数量、笔数、统计时优先调用本工具，禁止根据知识库片段臆测数字。"
            + "当前为骨架：未接入的 domain/metric 会返回未实现说明。";
    }

    @Override
    public JSONObject parametersSchema() {
        return JSONUtil.createObj()
            .set("type", "object")
            .set("properties", JSONUtil.createObj()
                .set("domain", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", "业务域：erp / oa / mall / store 等"))
                .set("metric", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", "指标：如 depot_out_pending、todo_count、order_today")))
            .set("required", new cn.hutool.json.JSONArray().put("domain").put("metric"));
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        String domain = args == null ? null : args.getStr("domain");
        String metric = args == null ? null : args.getStr("metric");
        if (StrUtil.isBlank(domain) || StrUtil.isBlank(metric)) {
            return JSONUtil.createObj().set("ok", false).set("error", "domain 与 metric 必填").toString();
        }
        return JSONUtil.createObj()
            .set("ok", false)
            .set("domain", domain)
            .set("metric", metric)
            .set("error", "业务指标尚未接入，请到对应业务页面查看；后续版本将对接 ERP/OA/商城接口")
            .toString();
    }
}
