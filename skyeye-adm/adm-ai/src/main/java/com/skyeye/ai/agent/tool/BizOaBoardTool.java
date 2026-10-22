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
import com.skyeye.rest.oa.board.service.IOaBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OA 待办看板快照：审批待办 / 我发起 / 日程待办（对齐 PC 仪表盘待办小部件）。
 * 按用户短缓存，避免连问重复打 Flowable/日程。
 */
@Component
public class BizOaBoardTool implements AiAgentTool {

    private static final long CACHE_TTL_MS = 30_000L;

    private static final Map<String, String> CODE_LABELS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("pending", "审批待办");
        m.put("initiated", "我发起的流程");
        m.put("schedule", "日程待办");
        CODE_LABELS = Collections.unmodifiableMap(m);
    }

    private final ConcurrentHashMap<String, CacheEntry> cacheByUser = new ConcurrentHashMap<>();

    @Autowired
    private IOaBoardService iOaBoardService;

    @Override
    public String name() {
        return "biz_oa_board";
    }

    @Override
    public String description() {
        return "查询 OA 待办看板快照（一次返回三类笔数，口径同仪表盘「待办任务」小部件）。"
            + "用户问待办有多少、审批待办、我发起的流程、日程待办数量时调用本工具；"
            + "从返回 counts 取值：pending=审批待办，initiated=我发起，schedule=日程待办。"
            + "不要臆测数字；不要为每个待办类型单独再调其它统计工具。";
    }

    @Override
    public JSONObject parametersSchema() {
        return JSONUtil.createObj()
            .set("type", "object")
            .set("properties", JSONUtil.createObj()
                .set("codes", JSONUtil.createObj()
                    .set("type", "array")
                    .set("items", JSONUtil.createObj().set("type", "string"))
                    .set("description", "可选。只要部分类型时传入，如 [\"pending\",\"schedule\"]；不传则返回全部")))
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
                .set("domain", "oa")
                .set("scope", "当前登录用户（与仪表盘待办小部件一致）")
                .set("hint", "pending=审批待办，initiated=我发起的流程，schedule=日程待办")
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
        String tenantId = context != null ? context.getTenantId() : null;
        String userId = context != null ? context.getUserId() : null;
        String cacheKey = StrUtil.blankToDefault(tenantId, "_") + ":" + StrUtil.blankToDefault(userId, "_");
        long now = System.currentTimeMillis();
        CacheEntry hit = cacheByUser.get(cacheKey);
        if (hit != null && now - hit.atMs < CACHE_TTL_MS) {
            return hit.board;
        }
        Map<String, Object> board = iOaBoardService.queryTodoBoardCounts();
        if (board == null) {
            board = Collections.emptyMap();
        }
        cacheByUser.put(cacheKey, new CacheEntry(board, now));
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
            String code = raw.trim().toLowerCase(Locale.ROOT);
            Object val = board.get(code);
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
