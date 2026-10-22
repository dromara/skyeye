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
import com.skyeye.rest.shop.board.service.IShopBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 商城看板：买家订单待办（queryMyOrderStat）；可选 storeId 合并门店经营统计。
 */
@Component
public class BizMallBoardTool implements AiAgentTool {

    private static final long CACHE_TTL_MS = 30_000L;

    private static final Map<String, String> CODE_LABELS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("waitPay", "待支付（买家）");
        m.put("waitDeliver", "待发货（买家）");
        m.put("waitReceive", "待收货（买家）");
        m.put("waitReview", "待评价（买家）");
        m.put("afterSale", "售后中（买家）");
        m.put("store_waitPay", "待支付（门店售出）");
        m.put("store_waitDeliver", "待发货（门店售出）");
        m.put("store_waitReceive", "待收货（门店售出）");
        m.put("store_afterSale", "售后中（门店）");
        m.put("store_todayOrder", "今日订单数（门店）");
        m.put("store_todayAmount", "今日成交额（门店）");
        CODE_LABELS = Collections.unmodifiableMap(m);
    }

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Autowired
    private IShopBoardService iShopBoardService;

    @Override
    public String name() {
        return "biz_mall_board";
    }

    @Override
    public String description() {
        return "查询商城订单/经营看板快照。"
            + "默认返回当前用户作为买家的订单待办（口径同商城「我的订单」角标 queryMyOrderStat）："
            + "waitPay/waitDeliver/waitReceive/waitReview/afterSale。"
            + "若已知门店 id，可传 storeId，额外合并门店经营统计（store_ 前缀，含今日订单 store_todayOrder 等）。"
            + "用户问待发货、待收货、今日订单等商城数目时调用；不要臆测数字。";
    }

    @Override
    public JSONObject parametersSchema() {
        return JSONUtil.createObj()
            .set("type", "object")
            .set("properties", JSONUtil.createObj()
                .set("storeId", JSONUtil.createObj()
                    .set("type", "string")
                    .set("description", "可选。门店 id；传入则额外返回门店经营相关 counts（store_ 前缀）"))
                .set("codes", JSONUtil.createObj()
                    .set("type", "array")
                    .set("items", JSONUtil.createObj().set("type", "string"))
                    .set("description", "可选。只要部分指标，如 [\"waitDeliver\",\"store_todayOrder\"]")))
            .set("required", new JSONArray());
    }

    @Override
    public String execute(JSONObject args, AiAgentToolContext context) {
        try {
            String storeId = args == null ? null : args.getStr("storeId");
            Map<String, Object> board = loadBoard(context, storeId);
            Map<String, Object> counts = filterCounts(board, args);
            Map<String, String> labels = new LinkedHashMap<>();
            for (String code : counts.keySet()) {
                labels.put(code, CODE_LABELS.getOrDefault(code, code));
            }
            return JSONUtil.createObj()
                .set("ok", true)
                .set("domain", "mall")
                .set("scope", StrUtil.isBlank(storeId)
                    ? "当前登录用户买家订单待办（queryMyOrderStat）"
                    : "买家订单待办 + 门店经营统计（storeId=" + storeId + "）")
                .set("hint", "买家：waitPay/waitDeliver/waitReceive/waitReview/afterSale；"
                    + "门店：store_waitDeliver、store_todayOrder、store_todayAmount 等")
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

    private Map<String, Object> loadBoard(AiAgentToolContext context, String storeId) {
        String userId = context != null ? context.getUserId() : null;
        String tenantId = context != null ? context.getTenantId() : null;
        String cacheKey = StrUtil.blankToDefault(tenantId, "_") + ":"
            + StrUtil.blankToDefault(userId, "_") + ":"
            + StrUtil.blankToDefault(storeId, "_");
        long now = System.currentTimeMillis();
        CacheEntry hit = cache.get(cacheKey);
        if (hit != null && now - hit.atMs < CACHE_TTL_MS) {
            return hit.board;
        }
        Map<String, Object> board = iShopBoardService.queryMallBoardCounts(storeId);
        if (board == null) {
            board = Collections.emptyMap();
        }
        cache.put(cacheKey, new CacheEntry(board, now));
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
            String code = raw.trim();
            Object val = board.get(code);
            if (val == null) {
                val = board.get(code.toLowerCase(Locale.ROOT));
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
