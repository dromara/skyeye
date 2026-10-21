/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.tool;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.agent.AiAgentTool;
import com.skyeye.ai.agent.AiAgentToolContext;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.erp.page.service.IErpPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 业务数目查询：按 domain+metric 分发。ERP 待出/待入对齐 erppage005（E01/E03）。
 */
@Component
public class BizCountGenericTool implements AiAgentTool {

    /** metric → 流程板节点 code */
    private static final Map<String, String> ERP_METRIC_CODES = new HashMap<>();

    static {
        ERP_METRIC_CODES.put("depot_out_pending", "E01");
        ERP_METRIC_CODES.put("wait_out", "E01");
        ERP_METRIC_CODES.put("e01", "E01");
        ERP_METRIC_CODES.put("depot_in_pending", "E03");
        ERP_METRIC_CODES.put("wait_put", "E03");
        ERP_METRIC_CODES.put("e03", "E03");
    }

    @Autowired
    private IErpPageService iErpPageService;

    @Override
    public String name() {
        return "biz_count_generic";
    }

    @Override
    public String description() {
        return "查询业务数目/统计。"
            + "ERP：domain=erp，metric=depot_out_pending（待出库笔数，对齐仪表盘出库管理）"
            + "或 depot_in_pending（待入库笔数，对齐仪表盘入库管理）。"
            + "涉及数量、笔数时必须调用本工具，禁止臆测数字。"
            + "未接入的 domain/metric 会返回未实现说明。";
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
                    .set("description", "指标：erp 用 depot_out_pending、depot_in_pending")))
            .set("required", new cn.hutool.json.JSONArray().put("domain").put("metric"));
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        String domain = args == null ? null : args.getStr("domain");
        String metric = args == null ? null : args.getStr("metric");
        if (StrUtil.isBlank(domain) || StrUtil.isBlank(metric)) {
            return JSONUtil.createObj().set("ok", false).set("error", "domain 与 metric 必填").toString();
        }
        String domainKey = domain.trim().toLowerCase(Locale.ROOT);
        String metricKey = metric.trim().toLowerCase(Locale.ROOT);
        try {
            if ("erp".equals(domainKey)) {
                return countErp(metricKey);
            }
            return notImplemented(domainKey, metricKey);
        } catch (CustomException e) {
            return JSONUtil.createObj()
                .set("ok", false)
                .set("domain", domainKey)
                .set("metric", metricKey)
                .set("error", e.getMessage())
                .toString();
        } catch (Exception e) {
            return JSONUtil.createObj()
                .set("ok", false)
                .set("domain", domainKey)
                .set("metric", metricKey)
                .set("error", StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()))
                .toString();
        }
    }

    private String countErp(String metricKey) {
        String code = ERP_METRIC_CODES.get(metricKey);
        if (StrUtil.isBlank(code)) {
            return notImplemented("erp", metricKey);
        }
        Map<String, Object> board = iErpPageService.queryProcessFlowCount();
        int count = readCount(board, code);
        String label = "E01".equals(code) ? "待出库" : "待入库";
        return JSONUtil.createObj()
            .set("ok", true)
            .set("domain", "erp")
            .set("metric", metricKey)
            .set("code", code)
            .set("label", label)
            .set("count", count)
            .set("scope", "当前租户全量（与仪表盘流程板一致，不按仓库筛选）")
            .toString();
    }

    private int readCount(Map<String, Object> board, String code) {
        if (board == null || board.isEmpty()) {
            return 0;
        }
        Object raw = board.get(code);
        if (raw == null) {
            raw = board.get(code.toLowerCase(Locale.ROOT));
        }
        if (raw == null) {
            return 0;
        }
        if (raw instanceof Number) {
            return ((Number) raw).intValue();
        }
        try {
            return Integer.parseInt(raw.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String notImplemented(String domain, String metric) {
        return JSONUtil.createObj()
            .set("ok", false)
            .set("domain", domain)
            .set("metric", metric)
            .set("error", "该业务指标尚未接入，请到对应业务页面查看")
            .set("supported", JSONUtil.createObj()
                .set("erp", JSONUtil.createArray()
                    .put("depot_out_pending")
                    .put("depot_in_pending")))
            .toString();
    }
}
