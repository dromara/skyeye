/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.boardconfig;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import com.skyeye.ai.agent.listconfig.AiBizListRemoteInvoker;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * biz_board 执行器：根据 {@link AiBizBoardDef} 远程取数，组装给模型的结果。
 * <p>
 * <b>主流程</b>：
 * <ol>
 *   <li>Registry 按 domain 取配置</li>
 *   <li>按 mode 加载整板 counts（带 30s 短缓存，避免连问反复打下游）</li>
 *   <li>若工具传了 codes，只保留这些指标</li>
 *   <li>拼 labels / scope / hint 返回</li>
 * </ol>
 * 远程调用复用 {@link AiBizListRemoteInvoker}（与 biz_list 同一套 Feign+URI）。
 */
@Component
public class AiBizBoardExecutor {

    /**
     * 同一 domain(+storeId) 结果缓存时长，减轻仪表盘连问压力
     */
    private static final long CACHE_TTL_MS = 30_000L;

    @Autowired
    private AiBizBoardRegistry registry;

    /**
     * 与列表共用的动态 Feign 调用（appId+serviceClassName → URI）
     */
    @Autowired
    private AiBizListRemoteInvoker remoteInvoker;

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    /**
     * @param domain      业务域 oa/erp/mall
     * @param storeId     可选；mall 且配置了 storeVariant 时追加门店指标
     * @param codesFilter 可选；只要部分 code，不传则整板
     * @return 含 domain/scope/hint/counts/labels 的 Map，由 Tool 再包成 JSON
     */
    public Map<String, Object> execute(String domain, String storeId, JSONArray codesFilter) {
        AiBizBoardDef def = registry.get(domain);
        if (def == null) {
            throw new CustomException("未配置的看板：domain=" + domain
                + "。请在 ai-biz-board-endpoints.json 或平台「AI业务接入」中增加 boards");
        }
        // 缓存键：域 + 门店（门店不同，mall 合并结果不同）
        String cacheKey = def.getDomain() + ":" + StrUtil.blankToDefault(storeId, "_");
        long now = System.currentTimeMillis();
        CacheEntry hit = cache.get(cacheKey);
        Map<String, Object> counts;
        if (hit != null && now - hit.atMs < CACHE_TTL_MS) {
            counts = hit.counts;
        } else {
            counts = loadCounts(def, storeId);
            cache.put(cacheKey, new CacheEntry(counts, now));
        }
        Map<String, Object> filtered = filterCodes(counts, codesFilter, def);
        Map<String, String> labels = buildLabels(def, filtered);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("domain", def.getDomain());
        result.put("scope", StrUtil.blankToDefault(def.getScope(), def.getDomain()));
        if (StrUtil.isNotBlank(storeId) && "mall".equals(def.getDomain())) {
            result.put("scope", "买家订单待办 + 门店经营统计（storeId=" + storeId.trim() + "）");
        }
        result.put("hint", StrUtil.blankToDefault(def.getHint(), ""));
        result.put("counts", filtered);
        result.put("labels", labels);
        return result;
    }

    /**
     * 按 mode 分支加载整板 counts
     */
    private Map<String, Object> loadCounts(AiBizBoardDef def, String storeId) {
        String mode = StrUtil.blankToDefault(def.getMode(), "").trim().toLowerCase(Locale.ROOT);
        switch (mode) {
            case "metrics":
                return loadMetrics(def);
            case "beanmap":
                return loadBeanMap(def);
            case "beanfields":
                return loadBeanFields(def, storeId);
            default:
                throw new CustomException("不支持的看板 mode=" + def.getMode());
        }
    }

    /**
     * metrics：逐项调接口；任一指标失败直接抛出，不伪装成 0。
     */
    private Map<String, Object> loadMetrics(AiBizBoardDef def) {
        Map<String, Object> counts = new LinkedHashMap<>();
        if (CollectionUtil.isEmpty(def.getMetrics())) {
            return counts;
        }
        Map<String, Object> baseParams = def.getFixedParams() == null
            ? new HashMap<>() : new HashMap<>(def.getFixedParams());
        for (AiBizBoardMetric metric : def.getMetrics()) {
            if (metric == null || StrUtil.isBlank(metric.getCode())) {
                continue;
            }
            counts.put(metric.getCode(), metricValue(metric, baseParams));
        }
        return counts;
    }

    private Object metricValue(AiBizBoardMetric metric, Map<String, Object> baseParams) {
        ResultEntity entity = remoteInvoker.invoke(
            metric.getAppId(),
            metric.getServiceClassName(),
            metric.getSpringApplicationName(),
            metric.getApiId(),
            new HashMap<>(baseParams));
        String from = StrUtil.blankToDefault(metric.getValueFrom(), "total").trim();
        if ("total".equalsIgnoreCase(from)) {
            return entity == null || entity.getTotal() == null ? 0 : entity.getTotal();
        }
        // valueFrom = bean.字段名
        if (from.startsWith("bean.") && entity != null && entity.getBean() != null) {
            Object v = entity.getBean().get(from.substring(5));
            return v == null ? 0 : v;
        }
        return 0;
    }

    /**
     * beanMap：一次调用，bean 整体当作 counts（如 ERP 流程板各 code）。
     */
    private Map<String, Object> loadBeanMap(AiBizBoardDef def) {
        ResultEntity entity = remoteInvoker.invoke(
            def.getAppId(),
            def.getServiceClassName(),
            def.getSpringApplicationName(),
            def.getApiId(),
            new HashMap<>());
        if (entity == null || entity.getBean() == null) {
            return Collections.emptyMap();
        }
        return new LinkedHashMap<>(entity.getBean());
    }

    /**
     * beanFields：主接口按 fieldMap 映射；有 storeId 再打 storeVariant，合并进 counts。
     */
    private Map<String, Object> loadBeanFields(AiBizBoardDef def, String storeId) {
        Map<String, Object> counts = new LinkedHashMap<>();
        ResultEntity entity = remoteInvoker.invoke(
            def.getAppId(),
            def.getServiceClassName(),
            def.getSpringApplicationName(),
            def.getApiId(),
            new HashMap<>());
        putMappedFields(counts, entity == null ? null : entity.getBean(), def.getFieldMap());

        if (StrUtil.isNotBlank(storeId) && def.getStoreVariant() != null) {
            AiBizBoardStoreVariant sv = def.getStoreVariant();
            Map<String, Object> params = new HashMap<>();
            if (sv.getArgBindings() != null) {
                for (Map.Entry<String, String> b : sv.getArgBindings().entrySet()) {
                    if ("storeId".equals(b.getKey()) && StrUtil.isNotBlank(b.getValue())) {
                        params.put(b.getValue(), storeId.trim());
                    }
                }
            }
            ResultEntity storeEntity = remoteInvoker.invoke(
                def.getAppId(),
                def.getServiceClassName(),
                def.getSpringApplicationName(),
                StrUtil.blankToDefault(sv.getApiId(), def.getApiId()),
                params);
            putMappedFields(counts, storeEntity == null ? null : storeEntity.getBean(), sv.getFieldMap());
        }
        return counts;
    }

    /**
     * 按 fieldMap：bean[源字段] → counts[目标 code]；缺字段写 0。
     */
    private void putMappedFields(Map<String, Object> counts, Map<String, Object> bean,
                                 Map<String, String> fieldMap) {
        if (bean == null || fieldMap == null || fieldMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> e : fieldMap.entrySet()) {
            if (StrUtil.isBlank(e.getKey()) || StrUtil.isBlank(e.getValue())) {
                continue;
            }
            Object v = bean.get(e.getKey());
            counts.put(e.getValue(), v == null ? 0 : v);
        }
    }

    /**
     * 工具传了 codes 时做过滤；ERP 的 code 统一转大写再匹配。
     */
    private Map<String, Object> filterCodes(Map<String, Object> counts, JSONArray codes, AiBizBoardDef def) {
        if (counts == null || counts.isEmpty()) {
            return Collections.emptyMap();
        }
        if (codes == null || codes.isEmpty()) {
            return new LinkedHashMap<>(counts);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        boolean upper = "erp".equals(def.getDomain());
        for (int i = 0; i < codes.size(); i++) {
            String raw = codes.getStr(i);
            if (StrUtil.isBlank(raw)) {
                continue;
            }
            String code = upper ? raw.trim().toUpperCase(Locale.ROOT) : raw.trim();
            Object val = counts.get(code);
            if (val == null) {
                val = counts.get(raw.trim());
            }
            if (val == null && !upper) {
                val = counts.get(code.toLowerCase(Locale.ROOT));
            }
            out.put(code, val == null ? 0 : val);
        }
        return out;
    }

    /**
     * 为过滤后的每个 code 挂上中文名（配置 labels + metrics.label）。
     */
    private Map<String, String> buildLabels(AiBizBoardDef def, Map<String, Object> counts) {
        Map<String, String> labels = new LinkedHashMap<>();
        Map<String, String> configured = new LinkedHashMap<>();
        if (def.getLabels() != null) {
            configured.putAll(def.getLabels());
        }
        if (def.getMetrics() != null) {
            for (AiBizBoardMetric m : def.getMetrics()) {
                if (m != null && StrUtil.isNotBlank(m.getCode())) {
                    configured.put(m.getCode(), StrUtil.blankToDefault(m.getLabel(), m.getCode()));
                }
            }
        }
        for (String code : counts.keySet()) {
            labels.put(code, configured.getOrDefault(code, code));
        }
        return labels;
    }

    /**
     * 短缓存条目
     */
    private static final class CacheEntry {
        private final Map<String, Object> counts;
        private final long atMs;

        private CacheEntry(Map<String, Object> counts, long atMs) {
            this.counts = counts == null ? Collections.emptyMap() : new HashMap<>(counts);
            this.atMs = atMs;
        }
    }
}
