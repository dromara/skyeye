/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.boardconfig;

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
 * biz_board 配置注册中心：负责「读配置 → 按 domain 索引 → 给 Tool/Executor 查」。
 * <p>
 * <b>配置来源（优先级）</b>：
 * <ol>
 *   <li>平台基础信息 {@code settingData.ai.bizBoardEndpoints}（非空数组）——前端「AI业务接入」Tab 可改</li>
 *   <li>否则用 classpath 内置 {@code ai-biz-board-endpoints.json} 的 {@code boards} 数组</li>
 * </ol>
 * 每次 {@link #get}/{@link #domains} 都会重新问平台（平台设置有 Redis 缓存，开销可控），
 * 因此保存平台配置后无需重启 adm-ai。
 * <p>
 * <b>索引</b>：key = domain（小写；shop→mall），value = {@link AiBizBoardDef}。
 */
@Slf4j
@Component
public class AiBizBoardRegistry {

    /**
     * 内置默认配置文件（adm-ai 模块 resources 下）
     */
    private static final String CONFIG_PATH = "ai-biz-board-endpoints.json";

    /**
     * 平台 settingData.ai 下的键名，与 PlatformBaseSettingConst.KEY_BIZ_BOARD_ENDPOINTS 一致
     */
    private static final String PLATFORM_KEY = "bizBoardEndpoints";

    @Autowired
    private IPlatformBaseSettingRest iPlatformBaseSettingRest;

    /**
     * 启动时从 classpath 解析好的 domain→看板定义（兜底用）。
     */
    private Map<String, AiBizBoardDef> classpathByDomain = Collections.emptyMap();

    /**
     * 内置 boards 原始 JSON 数组，供管理端「加载内置默认」接口原样返回。
     */
    private JSONArray classpathBoardsRaw = new JSONArray();

    /**
     * 启动加载内置 JSON；失败则阻断启动（配置坏了看板全挂，尽早暴露）。
     */
    @PostConstruct
    public void loadClasspath() {
        try {
            ClassPathResource resource = new ClassPathResource(CONFIG_PATH);
            if (!resource.exists()) {
                log.warn("missing {}", CONFIG_PATH);
                return;
            }
            String json;
            try (InputStream in = resource.getInputStream()) {
                json = IoUtil.read(in, StandardCharsets.UTF_8);
            }
            JSONObject root = JSONUtil.parseObj(json);
            JSONArray arr = root.getJSONArray("boards");
            if (arr == null) {
                arr = new JSONArray();
            }
            this.classpathBoardsRaw = arr;
            this.classpathByDomain = indexBoards(arr);
            log.info("loaded {} biz_board domains from {}", classpathByDomain.size(), CONFIG_PATH);
        } catch (Exception e) {
            throw new IllegalStateException("load ai-biz-board-endpoints.json failed", e);
        }
    }

    /**
     * 当前生效的 domain→定义表：平台非空覆盖，否则 classpath。
     */
    private Map<String, AiBizBoardDef> currentByDomain() {
        try {
            Map<String, Object> bean = ExecuteFeignClient.get(
                () -> iPlatformBaseSettingRest.queryPlatformBizBoardEndpoints()).getBean();
            Object raw = bean == null ? null : bean.get(PLATFORM_KEY);
            JSONArray arr = toJsonArray(raw);
            if (arr != null && !arr.isEmpty()) {
                return indexBoards(arr);
            }
        } catch (Exception e) {
            log.warn("load biz_board from platform failed, use classpath: {}", e.getMessage());
        }
        return classpathByDomain;
    }

    /**
     * 把平台返回的 List / JSON 字符串等统一成 JSONArray
     */
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

    /**
     * 把 boards 数组转成 Map；缺 domain/mode 的条目跳过。
     */
    private Map<String, AiBizBoardDef> indexBoards(JSONArray arr) {
        Map<String, AiBizBoardDef> map = new LinkedHashMap<>();
        if (arr == null) {
            return map;
        }
        for (int i = 0; i < arr.size(); i++) {
            JSONObject raw = arr.getJSONObject(i);
            if (raw == null) {
                continue;
            }
            AiBizBoardDef def = JSONUtil.toBean(raw, AiBizBoardDef.class);
            if (def == null || StrUtil.isBlank(def.getDomain()) || StrUtil.isBlank(def.getMode())) {
                log.warn("skip invalid board index={}", i);
                continue;
            }
            String domain = def.getDomain().trim().toLowerCase(Locale.ROOT);
            // 兼容模型/旧配置写 shop
            if ("shop".equals(domain)) {
                domain = "mall";
            }
            def.setDomain(domain);
            map.put(domain, def);
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * 按业务域取看板定义；未配置返回 null（Executor 会抛「未配置的看板」）。
     */
    public AiBizBoardDef get(String domain) {
        if (StrUtil.isBlank(domain)) {
            return null;
        }
        String d = domain.trim().toLowerCase(Locale.ROOT);
        if ("shop".equals(d)) {
            d = "mall";
        }
        return currentByDomain().get(d);
    }

    /**
     * 当前生效配置里已有的全部 domain，供工具 parametersSchema / 错误提示
     */
    public Set<String> domains() {
        return currentByDomain().keySet();
    }

    /**
     * 拼给大模型看的工具 description：列出各 domain 下有哪些 code，引导一次按域查整板。
     */
    public String buildToolDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("查询业务看板笔数快照（只读，配置驱动，一次按域返回多项 counts）。");
        for (String d : domains()) {
            AiBizBoardDef def = get(d);
            if (def == null) {
                continue;
            }
            sb.append(d).append("：");
            if (def.getMetrics() != null && !def.getMetrics().isEmpty()) {
                List<String> codes = new ArrayList<>();
                for (AiBizBoardMetric m : def.getMetrics()) {
                    if (m != null && StrUtil.isNotBlank(m.getCode())) {
                        codes.add(m.getCode());
                    }
                }
                sb.append(String.join("/", codes));
            } else if (def.getLabels() != null && !def.getLabels().isEmpty()) {
                sb.append(String.join("/", def.getLabels().keySet()));
            }
            sb.append("；");
        }
        sb.append("商城有门店 id 时可传 storeId。不要臆测数字；不要为每个指标再调其它统计工具。");
        return sb.toString();
    }

    /**
     * 内置 boards 列表（平台页「加载内置默认」用）。
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getClasspathBoards() {
        if (classpathBoardsRaw == null || classpathBoardsRaw.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map> list = JSONUtil.toList(classpathBoardsRaw.toString(), Map.class);
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
     * 当前生效来源，供管理端展示标签：platform | classpath
     */
    public String currentSource() {
        try {
            Map<String, Object> bean = ExecuteFeignClient.get(
                () -> iPlatformBaseSettingRest.queryPlatformBizBoardEndpoints()).getBean();
            Object raw = bean == null ? null : bean.get(PLATFORM_KEY);
            JSONArray arr = toJsonArray(raw);
            if (arr != null && !arr.isEmpty()) {
                return "platform";
            }
        } catch (Exception ignored) {
            // 平台不可达时视为走内置
        }
        return "classpath";
    }
}
