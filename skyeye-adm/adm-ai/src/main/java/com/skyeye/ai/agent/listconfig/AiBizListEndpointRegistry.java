/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.listconfig;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.pro.rest.IPlatformBaseSettingRest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * biz_list 配置注册中心：读配置并按 {@code domain|type|variant} 索引。
 * <p>
 * <b>配置来源</b>：平台 {@code settingData.ai.bizListEndpoints}（非空）优先，
 * 否则 classpath {@code ai-biz-list-endpoints.json} 的 endpoints。
 * 与 {@link com.skyeye.ai.agent.boardconfig.AiBizBoardRegistry} 同套路。
 * <p>
 * domain 只做业务域索引，不参与 Feign 发现；真正路由看每条里的 appId+serviceClassName。
 */
@Slf4j
@Component
public class AiBizListEndpointRegistry {

    private static final String CONFIG_PATH = "ai-biz-list-endpoints.json";
    private static final String PLATFORM_KEY = "bizListEndpoints";

    @Autowired
    private IPlatformBaseSettingRest iPlatformBaseSettingRest;

    /**
     * 内置默认（classpath），启动时加载一次
     */
    private IndexedEndpoints classpathIndex = IndexedEndpoints.empty();

    /**
     * 内置默认的 endpoints 原始数组（供管理端「恢复默认」）
     */
    private JSONArray classpathEndpointsRaw = new JSONArray();

    @PostConstruct
    public void loadClasspath() {
        try {
            ClassPathResource resource = new ClassPathResource(CONFIG_PATH);
            if (!resource.exists()) {
                log.warn("missing {}, biz_list classpath defaults empty", CONFIG_PATH);
                return;
            }
            String json;
            try (InputStream in = resource.getInputStream()) {
                json = IoUtil.read(in, StandardCharsets.UTF_8);
            }
            JSONObject root = JSONUtil.parseObj(json);
            JSONArray arr = root.getJSONArray("endpoints");
            if (arr == null) {
                arr = new JSONArray();
            }
            this.classpathEndpointsRaw = arr;
            this.classpathIndex = indexEndpoints(arr, "classpath");
            log.info("loaded {} biz_list endpoint keys from {}", classpathIndex.byKey.size(), CONFIG_PATH);
        } catch (Exception e) {
            log.error("load {} failed: {}", CONFIG_PATH, e.getMessage());
            throw new IllegalStateException("load ai-biz-list-endpoints.json failed", e);
        }
    }

    /**
     * 当前生效索引：平台非空覆盖，否则内置
     */
    private IndexedEndpoints current() {
        try {
            Map<String, Object> bean = ExecuteFeignClient.get(
                () -> iPlatformBaseSettingRest.queryPlatformBizListEndpoints()).getBean();
            Object raw = bean == null ? null : bean.get(PLATFORM_KEY);
            JSONArray arr = toJsonArray(raw);
            if (arr != null && !arr.isEmpty()) {
                return indexEndpoints(arr, "platform");
            }
        } catch (Exception e) {
            log.warn("load biz_list from platform failed, use classpath: {}", e.getMessage());
        }
        return classpathIndex;
    }

    private static JSONArray toJsonArray(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof JSONArray) {
            return (JSONArray) raw;
        }
        if (raw instanceof List) {
            return JSONUtil.parseArray(JSONUtil.toJsonStr(raw));
        }
        if (raw instanceof String && StrUtil.isNotBlank((String) raw)) {
            return JSONUtil.parseArray((String) raw);
        }
        return null;
    }

    private IndexedEndpoints indexEndpoints(JSONArray arr, String source) {
        Map<String, AiBizListEndpoint> map = new LinkedHashMap<>();
        Map<String, Set<String>> domainTypes = new LinkedHashMap<>();
        if (arr == null || arr.isEmpty()) {
            return new IndexedEndpoints(map, domainTypes, source);
        }
        for (int i = 0; i < arr.size(); i++) {
            JSONObject raw = arr.getJSONObject(i);
            if (raw == null) {
                continue;
            }
            AiBizListEndpoint ep = JSONUtil.toBean(raw, AiBizListEndpoint.class);
            if (ep == null || StrUtil.isBlank(ep.getDomain()) || StrUtil.isBlank(ep.getType())
                || StrUtil.isBlank(ep.getApiId())) {
                log.warn("skip invalid biz_list endpoint index={} source={}", i, source);
                continue;
            }
            if (StrUtil.isBlank(ep.getServiceClassName())) {
                log.warn("skip endpoint {}.{} : need serviceClassName", ep.getDomain(), ep.getType());
                continue;
            }
            String domain = ep.getDomain().trim().toLowerCase(Locale.ROOT);
            String type = ep.getType().trim();
            String variant = StrUtil.blankToDefault(ep.getVariant(), "default").trim().toLowerCase(Locale.ROOT);
            ep.setDomain(domain);
            ep.setType(type);
            ep.setVariant(variant);
            map.put(key(domain, type, variant), ep);
            domainTypes.computeIfAbsent(domain, k -> new LinkedHashSet<>()).add(type);
            if (CollectionUtil.isNotEmpty(ep.getAliases())) {
                for (String alias : ep.getAliases()) {
                    if (StrUtil.isNotBlank(alias)) {
                        map.put(key(domain, alias.trim(), variant), ep);
                    }
                }
            }
        }
        return new IndexedEndpoints(
            Collections.unmodifiableMap(map),
            Collections.unmodifiableMap(domainTypes),
            source);
    }

    public AiBizListEndpoint get(String domain, String type, String variant) {
        if (StrUtil.isBlank(domain) || StrUtil.isBlank(type)) {
            return null;
        }
        IndexedEndpoints idx = current();
        String d = domain.trim().toLowerCase(Locale.ROOT);
        String t = type.trim();
        String v = StrUtil.blankToDefault(variant, "default").trim().toLowerCase(Locale.ROOT);
        AiBizListEndpoint hit = idx.byKey.get(key(d, t, v));
        if (hit != null) {
            return hit;
        }
        hit = idx.byKey.get(key(d, t.toLowerCase(Locale.ROOT), v));
        if (hit != null) {
            return hit;
        }
        for (Map.Entry<String, AiBizListEndpoint> e : idx.byKey.entrySet()) {
            if (e.getKey().startsWith(d + "|") && e.getKey().endsWith("|" + v)
                && e.getValue().getType().equalsIgnoreCase(t)) {
                return e.getValue();
            }
        }
        return null;
    }

    public Map<String, List<String>> supported() {
        IndexedEndpoints idx = current();
        Map<String, List<String>> out = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> e : idx.typesByDomain.entrySet()) {
            out.put(e.getKey(), new ArrayList<>(e.getValue()));
        }
        return out;
    }

    public String buildToolDescription() {
        IndexedEndpoints idx = current();
        StringBuilder sb = new StringBuilder();
        sb.append("分页查询业务列表（只读，配置驱动）。");
        for (Map.Entry<String, Set<String>> e : idx.typesByDomain.entrySet()) {
            sb.append(e.getKey()).append("：type=")
                .append(String.join("/", e.getValue())).append("；");
        }
        sb.append("商城有门店 id 时传 storeId 查门店售出。");
        sb.append("笔数用 biz_board（传 domain）。");
        sb.append("必须传 page（从1）与 limit（默认5，最大10）。");
        sb.append("最终 JSON 带 list:{title,items}。");
        return sb.toString();
    }

    public String buildTypeSchemaDescription() {
        IndexedEndpoints idx = current();
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Set<String>> e : idx.typesByDomain.entrySet()) {
            parts.add(e.getKey() + "：" + String.join(" / ", e.getValue()));
        }
        return String.join("；", parts);
    }

    public Set<String> domains() {
        return current().typesByDomain.keySet();
    }

    /**
     * 内置默认 endpoints（管理端恢复默认用）
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getClasspathEndpoints() {
        if (classpathEndpointsRaw == null || classpathEndpointsRaw.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map> list = JSONUtil.toList(classpathEndpointsRaw.toString(), Map.class);
        List<Map<String, Object>> out = new ArrayList<>();
        if (list != null) {
            for (Map m : list) {
                if (m != null) {
                    out.add((Map<String, Object>) m);
                }
            }
        }
        return out;
    }

    /**
     * 当前生效来源：platform / classpath
     */
    public String currentSource() {
        return current().source;
    }

    private static String key(String domain, String type, String variant) {
        return domain + "|" + type + "|" + variant;
    }

    private static final class IndexedEndpoints {
        private final Map<String, AiBizListEndpoint> byKey;
        private final Map<String, Set<String>> typesByDomain;
        private final String source;

        private IndexedEndpoints(Map<String, AiBizListEndpoint> byKey,
                                 Map<String, Set<String>> typesByDomain,
                                 String source) {
            this.byKey = byKey;
            this.typesByDomain = typesByDomain;
            this.source = source;
        }

        private static IndexedEndpoints empty() {
            return new IndexedEndpoints(Collections.emptyMap(), Collections.emptyMap(), "empty");
        }
    }
}
