/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.tool;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.agent.AiAgentTool;
import com.skyeye.ai.agent.AiAgentToolContext;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.erp.page.service.IErpPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ERP 流程板快照：一次 Feign erppage005，返回全部角标笔数（含待出库 E01、待入库 E03 等）。
 * 同租户短缓存，避免连问多次重复打 ERP。
 */
@Component
public class BizErpBoardTool implements AiAgentTool {

    private static final long CACHE_TTL_MS = 30_000L;

    /** 与 ErpPageServiceImpl 注释口径对齐 */
    private static final Map<String, String> CODE_LABELS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("B01", "销售订单");
        m.put("B02", "销售出库单");
        m.put("B03", "销售退货单");
        m.put("B04", "销售换货单");
        m.put("C01", "零售出库单");
        m.put("C02", "零售退货单");
        m.put("D01", "借出申请单");
        m.put("D02", "借出出库单");
        m.put("D03", "归还申请单");
        m.put("D04", "归还入库单");
        m.put("E01", "待出库");
        m.put("E02", "仓库出库单");
        m.put("E03", "待入库");
        m.put("E04", "仓库入库单");
        m.put("G01", "出货计划单");
        m.put("G02", "生产计划单");
        m.put("G03", "加工单");
        m.put("G04", "车间任务");
        m.put("I01", "领料单");
        m.put("I02", "领料出库单");
        m.put("I03", "补料单");
        m.put("I04", "补料出库单");
        m.put("I05", "退料单");
        m.put("I06", "退料入库单");
        CODE_LABELS = Collections.unmodifiableMap(m);
    }

    private final ConcurrentHashMap<String, CacheEntry> cacheByTenant = new ConcurrentHashMap<>();

    @Autowired
    private IErpPageService iErpPageService;

    @Override
    public String name() {
        return "biz_erp_board";
    }

    @Override
    public String description() {
        return "查询 ERP 门户流程板统计快照（一次返回全部角标笔数，口径同仪表盘 erppage005）。"
            + "用户问待出库/待入库/销售订单笔数等 ERP 数目时调用本工具；从返回的 counts 中取对应 code："
            + "E01=待出库，E03=待入库，其余见 labels。"
            + "不要臆测数字；不要为每个指标单独再调其它统计工具。";
    }

    @Override
    public JSONObject parametersSchema() {
        return JSONUtil.createObj()
            .set("type", "object")
            .set("properties", JSONUtil.createObj()
                .set("codes", JSONUtil.createObj()
                    .set("type", "array")
                    .set("items", JSONUtil.createObj().set("type", "string"))
                    .set("description", "可选。只要部分节点时传入，如 [\"E01\",\"E03\"]；不传则返回整板")))
            .set("required", new JSONArray());
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        try {
            Map<String, Object> board = loadBoard(context);
            Map<String, Object> counts = filterCounts(board, args);
            Map<String, String> labels = new LinkedHashMap<>();
            for (String code : counts.keySet()) {
                labels.put(code, CODE_LABELS.getOrDefault(code, code));
            }
            return JSONUtil.createObj()
                .set("ok", true)
                .set("domain", "erp")
                .set("scope", "当前租户全量（与 ERP 仪表盘流程板一致）")
                .set("hint", "E01=待出库笔数，E03=待入库笔数；回答时从 counts 取值并带上 labels 中文名")
                .set("counts", counts)
                .set("labels", labels)
                .toString();
        } catch (CustomException e) {
            return JSONUtil.createObj().set("ok", false).set("error", e.getMessage()).toString();
        } catch (Exception e) {
            return JSONUtil.createObj()
                .set("ok", false)
                .set("error", StrUtil.blankToDefault(e.getMessage(), e.getClass().getSimpleName()))
                .toString();
        }
    }

    private Map<String, Object> loadBoard(AiAgentToolContext context) {
        String cacheKey = StrUtil.blankToDefault(
            context != null ? context.getTenantId() : null, "_default");
        long now = System.currentTimeMillis();
        CacheEntry hit = cacheByTenant.get(cacheKey);
        if (hit != null && now - hit.atMs < CACHE_TTL_MS) {
            return hit.board;
        }
        Map<String, Object> board = iErpPageService.queryProcessFlowCount();
        if (board == null) {
            board = Collections.emptyMap();
        }
        cacheByTenant.put(cacheKey, new CacheEntry(board, now));
        return board;
    }

    private Map<String, Object> filterCounts(Map<String, Object> board, JSONObject args) {
        if (board == null || board.isEmpty()) {
            return Collections.emptyMap();
        }
        JSONArray codes = args == null ? null : args.getJSONArray("codes");
        if (CollectionUtil.isEmpty(codes)) {
            return new LinkedHashMap<>(board);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        for (int i = 0; i < codes.size(); i++) {
            String raw = codes.getStr(i);
            if (StrUtil.isBlank(raw)) {
                continue;
            }
            String code = raw.trim().toUpperCase(Locale.ROOT);
            Object val = board.get(code);
            if (val == null) {
                val = board.get(raw.trim());
            }
            out.put(code, val == null ? 0 : val);
        }
        return out;
    }

    private static final class CacheEntry {
        private final Map<String, Object> board;
        private final long atMs;

        private CacheEntry(Map<String, Object> board, long atMs) {
            this.board = board == null ? Collections.emptyMap() : new HashMap<>(board);
            this.atMs = atMs;
        }
    }
}
