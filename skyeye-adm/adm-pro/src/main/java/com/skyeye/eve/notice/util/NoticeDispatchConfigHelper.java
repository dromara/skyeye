/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.eve.notice.classenum.NoticeChannelEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: NoticeDispatchConfigHelper
 * @Description: 通知分发配置解析。单租户解析系统基础设置；多租户合并平台配置与租户模板
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/29 22:30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public final class NoticeDispatchConfigHelper {

    private NoticeDispatchConfigHelper() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseConfig(Object raw) {
        if (raw == null) {
            return defaultConfig();
        }
        if (raw instanceof Map) {
            Map<String, Object> config = (Map<String, Object>) raw;
            if (CollectionUtil.isEmpty(config) || !config.containsKey("types")) {
                return defaultConfig();
            }
            return config;
        }
        if (raw instanceof String && StrUtil.isNotBlank(raw.toString())) {
            Map<String, Object> config = JSONUtil.toBean(raw.toString(), null);
            if (CollectionUtil.isEmpty(config) || !config.containsKey("types")) {
                return defaultConfig();
            }
            return config;
        }
        return defaultConfig();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> findType(Map<String, Object> config, String typeCode) {
        List<Map<String, Object>> types = (List<Map<String, Object>>) config.getOrDefault("types", new ArrayList<>());
        for (Object item : types) {
            Map<String, Object> type = item instanceof Map ? (Map<String, Object>) item : JSONUtil.toBean(JSONUtil.toJsonStr(item), null);
            if (typeCode.equals(MapUtil.getStr(type, "codeNum"))
                && EnableEnum.ENABLE_USING.getKey().equals(MapUtil.getInt(type, "enabled"))) {
                return type;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> enabledChannels(Map<String, Object> type) {
        Object channels = type.get("enabledChannels");
        if (channels instanceof List) {
            return ((List<?>) channels).stream()
                .map(item -> Integer.valueOf(item.toString()))
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> findTemplate(Map<String, Object> type, Integer channel) {
        List<Map<String, Object>> templates = (List<Map<String, Object>>) type.getOrDefault("templates", new ArrayList<>());
        for (Object item : templates) {
            Map<String, Object> template = item instanceof Map ? (Map<String, Object>) item : JSONUtil.toBean(JSONUtil.toJsonStr(item), null);
            Integer tplChannel = MapUtil.getInt(template, "channel");
            Integer enabled = MapUtil.getInt(template, "enabled", EnableEnum.ENABLE_USING.getKey());
            if (channel.equals(tplChannel) && EnableEnum.ENABLE_USING.getKey().equals(enabled)) {
                return template;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> findChannelConfig(Map<String, Object> config, Integer channel) {
        List<Map<String, Object>> channels = (List<Map<String, Object>>) config.getOrDefault("channels", new ArrayList<>());
        Map<String, Object> found = null;
        for (Object item : channels) {
            Map<String, Object> channelCfg = item instanceof Map ? (Map<String, Object>) item
                : JSONUtil.toBean(JSONUtil.toJsonStr(item), null);
            Integer cfgChannel = MapUtil.getInt(channelCfg, "channel");
            Integer enabled = MapUtil.getInt(channelCfg, "enabled", EnableEnum.ENABLE_USING.getKey());
            if (channel.equals(cfgChannel) && !Integer.valueOf(2).equals(enabled)) {
                found = enrichWechatChannelCredential(config, channelCfg);
                break;
            }
        }
        // 公众号/小程序：渠道行缺失或行内无凭证时，直接用页签 wechatMp / wechatMini
        if (NoticeChannelEnum.MP.getKey().equals(channel) || NoticeChannelEnum.MINI_PROGRAM.getKey().equals(channel)) {
            if (found != null && StrUtil.isNotBlank(MapUtil.getStr(found, "appId"))
                && StrUtil.isNotBlank(MapUtil.getStr(found, "appSecret"))) {
                return found;
            }
            Map<String, Object> fromTab = buildWechatChannelFromTab(config, channel);
            if (fromTab != null) {
                if (found == null) {
                    return fromTab;
                }
                // 保留渠道行其它字段，凭证以页签为准
                Map<String, Object> merged = new HashMap<>(found);
                merged.put("appId", MapUtil.getStr(fromTab, "appId"));
                merged.put("appSecret", MapUtil.getStr(fromTab, "appSecret"));
                merged.put("guideImage", MapUtil.getStr(fromTab, "guideImage"));
                return merged;
            }
        }
        return found;
    }

    /**
     * 公众号/小程序：凭证取 wechatMp / wechatMini 页签配置。
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> enrichWechatChannelCredential(Map<String, Object> config,
                                                                     Map<String, Object> channelCfg) {
        if (channelCfg == null || config == null) {
            return channelCfg;
        }
        Integer channel = MapUtil.getInt(channelCfg, "channel");
        if (!Integer.valueOf(4).equals(channel) && !Integer.valueOf(5).equals(channel)) {
            return channelCfg;
        }
        Map<String, Object> account = resolveWechatAccount(config, channel);
        if (isWechatAccountUsable(account)) {
            Map<String, Object> enriched = new HashMap<>(channelCfg);
            enriched.put("appId", MapUtil.getStr(account, "appId"));
            enriched.put("appSecret", MapUtil.getStr(account, "appSecret"));
            enriched.put("guideImage", MapUtil.getStr(account, "guideImage"));
            return enriched;
        }
        return channelCfg;
    }

    /**
     * 仅用页签配置组装可用渠道凭证（可不依赖 channels 行）
     */
    private static Map<String, Object> buildWechatChannelFromTab(Map<String, Object> config, Integer channel) {
        Map<String, Object> account = resolveWechatAccount(config, channel);
        if (!isWechatAccountUsable(account)) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("channel", channel);
        result.put("enabled", EnableEnum.ENABLE_USING.getKey());
        result.put("appId", MapUtil.getStr(account, "appId"));
        result.put("appSecret", MapUtil.getStr(account, "appSecret"));
        result.put("guideImage", MapUtil.getStr(account, "guideImage"));
        return result;
    }

    private static boolean isWechatAccountUsable(Map<String, Object> account) {
        if (account == null) {
            return false;
        }
        if (StrUtil.isBlank(MapUtil.getStr(account, "appId"))
            || StrUtil.isBlank(MapUtil.getStr(account, "appSecret"))) {
            return false;
        }
        // 2 = 禁用；其余（含空）视为启用
        return !EnableEnum.DISABLE_USING.getKey().equals(MapUtil.getInt(account, "enabled", EnableEnum.ENABLE_USING.getKey()));
    }

    private static Map<String, Object> resolveWechatAccount(Map<String, Object> config, Integer channel) {
        String objectKey = NoticeChannelEnum.MP.getKey().equals(channel) ? "wechatMp" : "wechatMini";
        return asStringObjectMap(config.get(objectKey));
    }

    /**
     * Map / JSONObject / JSON 字符串 → Map；空对象返回 null
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> asStringObjectMap(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) raw;
            return map.isEmpty() ? null : map;
        }
        if (raw instanceof String && StrUtil.isNotBlank(raw.toString())) {
            Map<String, Object> parsed = JSONUtil.toBean(raw.toString(), null);
            return CollectionUtil.isEmpty(parsed) ? null : parsed;
        }
        try {
            Map<String, Object> parsed = JSONUtil.toBean(JSONUtil.toJsonStr(raw), null);
            return CollectionUtil.isEmpty(parsed) ? null : parsed;
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * 多租户：合并平台类型清单/渠道账号与租户模板。
     * codeNum、name 以平台为准；remark/enabledChannels/templates 取租户；渠道账号只读平台。
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> mergePlatformAndTenant(Map<String, Object> platform, Object tenantRaw) {
        Map<String, Object> tenant = parseConfig(tenantRaw);
        List<Map<String, Object>> platformTypes =
            (List<Map<String, Object>>) platform.getOrDefault("types", new ArrayList<>());
        List<Map<String, Object>> tenantTypes =
            (List<Map<String, Object>>) tenant.getOrDefault("types", new ArrayList<>());
        Map<String, Map<String, Object>> tenantByCode = new HashMap<>();
        for (Object item : tenantTypes) {
            Map<String, Object> type = item instanceof Map ? (Map<String, Object>) item
                : JSONUtil.toBean(JSONUtil.toJsonStr(item), null);
            String code = MapUtil.getStr(type, "codeNum");
            if (StrUtil.isNotBlank(code)) {
                tenantByCode.put(code, type);
            }
        }
        List<Map<String, Object>> mergedTypes = new ArrayList<>();
        for (Object item : platformTypes) {
            Map<String, Object> platformType = item instanceof Map ? (Map<String, Object>) item
                : JSONUtil.toBean(JSONUtil.toJsonStr(item), null);
            if (!EnableEnum.ENABLE_USING.getKey().equals(MapUtil.getInt(platformType, "enabled",
                EnableEnum.ENABLE_USING.getKey()))) {
                continue;
            }
            String code = MapUtil.getStr(platformType, "codeNum");
            Map<String, Object> tenantType = tenantByCode.get(code);
            Map<String, Object> merged = new HashMap<>();
            merged.put("codeNum", code);
            merged.put("name", MapUtil.getStr(platformType, "name"));
            merged.put("enabled", tenantType == null
                ? EnableEnum.ENABLE_USING.getKey()
                : MapUtil.getInt(tenantType, "enabled", EnableEnum.ENABLE_USING.getKey()));
            Map<String, Object> defaults = defaultTypeByCode(code);
            if (tenantType != null) {
                merged.put("remark", MapUtil.getStr(tenantType, "remark",
                    defaults == null ? StrUtil.EMPTY : MapUtil.getStr(defaults, "remark")));
                Object channels = tenantType.get("enabledChannels");
                if (channels instanceof List && CollectionUtil.isNotEmpty((List<?>) channels)) {
                    merged.put("enabledChannels", channels);
                } else if (defaults != null) {
                    merged.put("enabledChannels", defaults.get("enabledChannels"));
                } else {
                    merged.put("enabledChannels", new ArrayList<>());
                }
                Object templates = tenantType.get("templates");
                if (templates instanceof List && CollectionUtil.isNotEmpty((List<?>) templates)) {
                    merged.put("templates", templates);
                } else if (defaults != null) {
                    merged.put("templates", defaults.get("templates"));
                } else {
                    merged.put("templates", new ArrayList<>());
                }
            } else if (defaults != null) {
                merged.put("remark", MapUtil.getStr(defaults, "remark"));
                merged.put("enabledChannels", defaults.get("enabledChannels"));
                merged.put("templates", defaults.get("templates"));
            } else {
                merged.put("remark", StrUtil.EMPTY);
                merged.put("enabledChannels", new ArrayList<>());
                merged.put("templates", new ArrayList<>());
            }
            // 多租户：平台模板号、跳转路径以平台为准，覆盖到各渠道模板
            applyPlatformApiTemplateIds(merged, platformType);
            applyPlatformJumpConfigs(merged, platformType);
            mergedTypes.add(merged);
        }
        Map<String, Object> config = new HashMap<>();
        config.put("types", mergedTypes);
        config.put("channels", platform.getOrDefault("channels", new ArrayList<>()));
        config.put("wechatMp", platform.getOrDefault("wechatMp", new HashMap<>()));
        config.put("wechatMini", platform.getOrDefault("wechatMini", new HashMap<>()));
        // 跳转域名仅来自平台，租户不可覆盖
        config.put("jumpDomains", normalizeJumpDomains(platform.get("jumpDomains")));
        return config;
    }

    public static Map<String, Object> defaultPlatformConfig() {
        Map<String, Object> config = new HashMap<>();
        List<Map<String, Object>> types = new ArrayList<>();
        types.add(buildPlatformType("SCHEDULE_MESSAGE", "日程消息"));
        types.add(buildPlatformType("PLAN_REMINDER", "计划提醒"));
        types.add(buildPlatformType("WORK_ORDER_REMINDER", "售后工单派工提醒"));
        types.add(buildPlatformType("EQUIPMENT_INSPECTION_REMINDER", "设备巡检派工提醒"));
        types.add(buildPlatformType("EQUIPMENT_REPAIR_REMINDER", "设备维修派工提醒"));
        types.add(buildPlatformType("ANNOUNCEMENT", "公告提醒"));
        config.put("types", types);
        config.put("channels", new ArrayList<>());
        config.put("wechatMp", new HashMap<>());
        config.put("wechatMini", new HashMap<>());
        config.put("jumpDomains", defaultJumpDomains());
        return config;
    }

    public static Map<String, Object> defaultJumpDomains() {
        Map<String, Object> domains = new HashMap<>();
        domains.put("h5Domain", StrUtil.EMPTY);
        domains.put("pcDomain", StrUtil.EMPTY);
        return domains;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> normalizeJumpDomains(Object raw) {
        Map<String, Object> domains = defaultJumpDomains();
        Map<String, Object> src = asStringObjectMap(raw);
        if (MapUtil.isEmpty(src)) {
            return domains;
        }
        domains.put("h5Domain", StrUtil.trim(MapUtil.getStr(src, "h5Domain", StrUtil.EMPTY)));
        domains.put("pcDomain", StrUtil.trim(MapUtil.getStr(src, "pcDomain", StrUtil.EMPTY)));
        return domains;
    }

    public static String getJumpDomain(Map<String, Object> config, String key) {
        Map<String, Object> domains = normalizeJumpDomains(config == null ? null : config.get("jumpDomains"));
        return MapUtil.getStr(domains, key, StrUtil.EMPTY);
    }

    /**
     * 相对路径拼接域名；已是 http(s) 或域名未配则原样返回。
     */
    public static String joinWithDomain(String domain, String path) {
        if (StrUtil.isBlank(path)) {
            return StrUtil.EMPTY;
        }
        String trimmedPath = path.trim();
        String lower = trimmedPath.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            return trimmedPath;
        }
        if (StrUtil.isBlank(domain)) {
            return trimmedPath;
        }
        String base = domain.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (trimmedPath.startsWith("/")) {
            return base + trimmedPath;
        }
        return base + "/" + trimmedPath;
    }

    private static Map<String, Object> buildPlatformType(String code, String name) {
        Map<String, Object> type = new HashMap<>();
        type.put("codeNum", code);
        type.put("name", name);
        type.put("enabled", EnableEnum.ENABLE_USING.getKey());
        type.put("apiTemplates", new ArrayList<>());
        return type;
    }

    @SuppressWarnings("unchecked")
    private static void applyPlatformApiTemplateIds(Map<String, Object> mergedType, Map<String, Object> platformType) {
        Object apiRaw = platformType.get("apiTemplates");
        if (!(apiRaw instanceof List) || CollectionUtil.isEmpty((List<?>) apiRaw)) {
            return;
        }
        Map<String, String> apiMap = new HashMap<>();
        for (Object item : (List<?>) apiRaw) {
            Map<String, Object> row = item instanceof Map ? (Map<String, Object>) item
                : JSONUtil.toBean(JSONUtil.toJsonStr(item), null);
            String channel = MapUtil.getStr(row, "channel");
            if (StrUtil.isNotBlank(channel)) {
                apiMap.put(channel, MapUtil.getStr(row, "apiTemplateId", StrUtil.EMPTY));
            }
        }
        if (apiMap.isEmpty()) {
            return;
        }
        Object templatesObj = mergedType.get("templates");
        if (!(templatesObj instanceof List)) {
            return;
        }
        List<Map<String, Object>> templates = new ArrayList<>();
        for (Object item : (List<?>) templatesObj) {
            Map<String, Object> template = item instanceof Map ? new HashMap<>((Map<String, Object>) item)
                : JSONUtil.toBean(JSONUtil.toJsonStr(item), null);
            String channel = MapUtil.getStr(template, "channel");
            if (apiMap.containsKey(channel)) {
                template.put("apiTemplateId", apiMap.get(channel));
            }
            templates.add(template);
        }
        mergedType.put("templates", templates);
    }

    /**
     * 多租户：跳转路径仅平台可配，覆盖到租户模板（站内信/公众号/小程序）。
     * 若平台未下发 jumpConfigs，则不改模板跳转字段。
     */
    @SuppressWarnings("unchecked")
    private static void applyPlatformJumpConfigs(Map<String, Object> mergedType, Map<String, Object> platformType) {
        Object jumpRaw = platformType.get("jumpConfigs");
        if (!(jumpRaw instanceof List) || CollectionUtil.isEmpty((List<?>) jumpRaw)) {
            if (jumpRaw instanceof String && StrUtil.isNotBlank(jumpRaw.toString())) {
                try {
                    Object parsed = JSONUtil.toBean(jumpRaw.toString(), null);
                    if (parsed instanceof List) {
                        jumpRaw = parsed;
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
            } else {
                return;
            }
        }
        Map<String, Map<String, Object>> jumpMap = new HashMap<>();
        for (Object item : (List<?>) jumpRaw) {
            Map<String, Object> row = item instanceof Map ? (Map<String, Object>) item
                : JSONUtil.toBean(JSONUtil.toJsonStr(item), null);
            if (row == null) {
                continue;
            }
            String channel = MapUtil.getStr(row, "channel");
            if (StrUtil.isNotBlank(channel)) {
                jumpMap.put(channel, row);
            }
        }
        if (jumpMap.isEmpty()) {
            return;
        }
        Object templatesObj = mergedType.get("templates");
        if (!(templatesObj instanceof List)) {
            return;
        }
        List<Map<String, Object>> templates = new ArrayList<>();
        for (Object item : (List<?>) templatesObj) {
            Map<String, Object> template = item instanceof Map ? new HashMap<>((Map<String, Object>) item)
                : JSONUtil.toBean(JSONUtil.toJsonStr(item), null);
            String channel = MapUtil.getStr(template, "channel");
            Map<String, Object> jump = jumpMap.get(channel);
            if (jump != null) {
                if ("1".equals(channel)) {
                    template.put("pcPath", MapUtil.getStr(jump, "pcPath", StrUtil.EMPTY));
                    template.put("mobilePath", MapUtil.getStr(jump, "mobilePath", StrUtil.EMPTY));
                } else if ("4".equals(channel)) {
                    template.put("h5Url", MapUtil.getStr(jump, "h5Url", StrUtil.EMPTY));
                    template.put("pagePath", MapUtil.getStr(jump, "pagePath", StrUtil.EMPTY));
                } else if ("5".equals(channel)) {
                    template.put("pagePath", MapUtil.getStr(jump, "pagePath", StrUtil.EMPTY));
                }
            }
            templates.add(template);
        }
        mergedType.put("templates", templates);
    }

    private static Map<String, Object> defaultTypeByCode(String code) {
        Map<String, Object> defaults = defaultConfig();
        return findType(defaults, code);
    }

    public static Map<String, Object> defaultConfig() {
        Map<String, Object> config = new HashMap<>();
        List<Map<String, Object>> types = new ArrayList<>();
        types.add(buildType("SCHEDULE_MESSAGE", "日程消息",
            "您有一条新的日程信息，请及时阅读。", listOf(1, 2),
            listOf(
                buildTemplate(1, "日程-站内信", "日程消息",
                    "尊敬的{userName},您好：<br/>《{title}》时间为：{startTime} ~ {endTime}<br/>请注意安排好您的工作时间。"),
                buildTemplate(2, "日程-邮件", "日程消息",
                    "尊敬的{userName},您好：<br/>《{title}》时间为：{startTime} ~ {endTime}<br/>请注意安排好您的工作时间。")
            )));
        types.add(buildType("PLAN_REMINDER", "计划提醒", "计划提醒", listOf(1),
            listOf(buildTemplate(1, "计划-站内信", "计划提醒", "您有一条计划提醒：{title}"))));
        types.add(buildType("WORK_ORDER_REMINDER", "售后工单派工提醒",
            "您有一条新的派工信息，请及时阅读。", listOf(1, 2),
            listOf(
                buildTemplate(1, "售后工单-站内信", "售后工单派工提醒",
                    "尊敬的{userName}，您好：<br/>您有一份待接单工单，工单号为：{orderNum}，请及时接单。"),
                buildTemplate(2, "售后工单-邮件", "售后工单派工提醒",
                    "尊敬的{userName}，您好：<br/>您有一份待接单工单，工单号为：{orderNum}，请及时接单。")
            )));
        types.add(buildType("EQUIPMENT_INSPECTION_REMINDER", "设备巡检派工提醒",
            "您有一条新的巡检派工信息，请及时阅读。", listOf(1, 2),
            listOf(
                buildTemplate(1, "巡检-站内信", "设备巡检派工提醒",
                    "尊敬的{userName}，您好：<br/>您有一份待接单巡检单，单号为：{orderNum}，请及时接单。"),
                buildTemplate(2, "巡检-邮件", "设备巡检派工提醒",
                    "尊敬的{userName}，您好：<br/>您有一份待接单巡检单，单号为：{orderNum}，请及时接单。")
            )));
        types.add(buildType("EQUIPMENT_REPAIR_REMINDER", "设备维修派工提醒",
            "您有一条新的维修派工信息，请及时阅读。", listOf(1, 2),
            listOf(
                buildTemplate(1, "维修-站内信", "设备维修派工提醒",
                    "尊敬的{userName}，您好：<br/>您有一份待接单维修单，单号为：{orderNum}，请及时接单。"),
                buildTemplate(2, "维修-邮件", "设备维修派工提醒",
                    "尊敬的{userName}，您好：<br/>您有一份待接单维修单，单号为：{orderNum}，请及时接单。")
            )));
        types.add(buildType("ANNOUNCEMENT", "公告提醒", "内部公告邮件通知", listOf(2),
            listOf(buildTemplate(2, "公告-邮件", "公告提醒", "内部公告 -【{title}】"))));
        config.put("types", types);
        config.put("channels", new ArrayList<>());
        config.put("jumpDomains", defaultJumpDomains());
        return config;
    }

    private static Map<String, Object> buildType(String code, String name, String remark,
                                                 List<Integer> enabledChannels, List<Map<String, Object>> templates) {
        Map<String, Object> type = new HashMap<>();
        type.put("codeNum", code);
        type.put("name", name);
        type.put("remark", remark);
        type.put("enabled", EnableEnum.ENABLE_USING.getKey());
        type.put("enabledChannels", enabledChannels);
        type.put("templates", templates);
        return type;
    }

    private static Map<String, Object> buildTemplate(int channel, String name, String title, String content) {
        Map<String, Object> template = new HashMap<>();
        template.put("channel", channel);
        template.put("name", name);
        template.put("title", title);
        template.put("content", content);
        template.put("apiTemplateId", StrUtil.EMPTY);
        template.put("smsTemplateCode", StrUtil.EMPTY);
        template.put("pagePath", StrUtil.EMPTY);
        template.put("h5Url", StrUtil.EMPTY);
        template.put("pcPath", StrUtil.EMPTY);
        template.put("mobilePath", StrUtil.EMPTY);
        template.put("enabled", EnableEnum.ENABLE_USING.getKey());
        return template;
    }

    @SafeVarargs
    private static <T> List<T> listOf(T... items) {
        List<T> list = new ArrayList<>();
        for (T item : items) {
            list.add(item);
        }
        return list;
    }

}
