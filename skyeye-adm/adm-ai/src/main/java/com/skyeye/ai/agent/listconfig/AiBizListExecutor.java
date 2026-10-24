/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.listconfig;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * biz_list 执行器：配置 → 远程列表 → 行字段瘦身成卡片 items。
 * <p>
 * <b>主流程</b>：
 * <ol>
 *   <li>用 domain + type + variant（有 storeId 则 store，否则 default）查 {@link AiBizListEndpoint}</li>
 *   <li>{@link #buildParams} 拼 page/limit、fixedParams、typeParamMap、storeId 绑定</li>
 *   <li>{@link AiBizListRemoteInvoker} 按 appId+serviceClassName 调下游</li>
 *   <li>{@link AiBizListRowMapper} 按 fields 配置抽出 title/subtitle/id，避免把整行大 JSON 塞给模型</li>
 * </ol>
 */
@Component
public class AiBizListExecutor {

    @Autowired
    private AiBizListEndpointRegistry registry;

    @Autowired
    private AiBizListRemoteInvoker remoteInvoker;

    /**
     * @param domain  逻辑业务域（oa/erp/mall），给模型选；不是微服务名
     * @param type    列表类型（可写 aliases，如 E01→depotOut）
     * @param storeId 可选，商城门店 id；有值时优先走 variant=store
     * @param page    页码（从 1）
     * @param limit   每页条数
     */
    public Map<String, Object> execute(String domain, String type, String storeId, int page, int limit) {
        String domainKey = StrUtil.blankToDefault(domain, "").trim().toLowerCase(Locale.ROOT);
        if ("shop".equals(domainKey)) {
            domainKey = "mall";
        }
        boolean storeSide = StrUtil.isNotBlank(storeId);
        String variant = storeSide ? "store" : "default";
        AiBizListEndpoint ep = registry.get(domainKey, type, variant);
        if (ep == null && storeSide) {
            // 未配门店变体时回退买家列表
            ep = registry.get(domainKey, type, "default");
            storeSide = false;
        }
        if (ep == null) {
            throw new CustomException("未配置的列表：domain=" + domainKey + ", type=" + type
                + "。请在 ai-biz-list-endpoints.json 或平台「AI业务接入」中增加 endpoints");
        }
        if (ep.isRequireStoreId() && StrUtil.isBlank(storeId)) {
            throw new CustomException("该列表需要 storeId（门店 id）");
        }

        Map<String, Object> params = buildParams(ep, type, storeId, page, limit);

        ResultEntity entity = remoteInvoker.invoke(
            ep.getAppId(),
            ep.getServiceClassName(),
            ep.getSpringApplicationName(),
            ep.getApiId(),
            params);
        List<Map<String, Object>> rows = entity == null || entity.getRows() == null
            ? Collections.emptyList() : entity.getRows();
        int total = entity == null || entity.getTotal() == null ? rows.size() : entity.getTotal();

        // 按 fields 配置瘦身，供前端 list 卡片与模型摘要
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            if (row != null) {
                items.add(AiBizListRowMapper.slim(row, ep.getFields()));
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("domain", ep.getDomain());
        result.put("type", ep.getType());
        result.put("scope", StrUtil.blankToDefault(ep.getScope(), ep.getDomain()));
        result.put("listTitle", StrUtil.blankToDefault(ep.getListTitle(), ep.getType()));
        result.put("total", total);
        result.put("page", params.get("page"));
        result.put("limit", params.get("limit"));
        result.put("items", items);
        if (storeSide && StrUtil.isNotBlank(storeId)) {
            result.put("storeId", storeId.trim());
        }
        return result;
    }

    /**
     * 组装下游请求体：固定参数 + 分页 +（可选）type 码映射 + storeId 绑定。
     */
    private Map<String, Object> buildParams(AiBizListEndpoint ep, String type, String storeId, int page, int limit) {
        Map<String, Object> params = new HashMap<>();
        if (ep.getFixedParams() != null) {
            params.putAll(ep.getFixedParams());
        }
        params.put("page", Math.max(page, 1));
        params.put("limit", Math.max(limit, 1));
        if (!params.containsKey("keyword")) {
            params.put("keyword", "");
        }

        // 商城等：工具 type → 接口 type 码
        if (ep.getTypeParamMap() != null && !ep.getTypeParamMap().isEmpty()) {
            String mapped = ep.getTypeParamMap().get(ep.getType());
            if (mapped == null) {
                mapped = ep.getTypeParamMap().get(type);
            }
            if (mapped == null) {
                for (Map.Entry<String, String> e : ep.getTypeParamMap().entrySet()) {
                    if (e.getKey() != null && e.getKey().equalsIgnoreCase(ep.getType())) {
                        mapped = e.getValue();
                        break;
                    }
                }
            }
            String key = StrUtil.blankToDefault(ep.getTypeParamKey(), "type");
            params.put(key, mapped == null ? "" : mapped);
        }

        if (ep.getArgBindings() != null && StrUtil.isNotBlank(storeId)) {
            for (Map.Entry<String, String> bind : ep.getArgBindings().entrySet()) {
                if ("storeId".equals(bind.getKey()) && StrUtil.isNotBlank(bind.getValue())) {
                    params.put(bind.getValue(), storeId.trim());
                }
            }
        }
        return params;
    }
}
