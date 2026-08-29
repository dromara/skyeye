/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.util;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.tenant.TenantTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 知识库正文中的租户标记，以及问答时按当前租户过滤检索结果。
 */
public final class KnowledgeTenantFilterHelper {

    public static final String MARKER_TENANT = "skyeye_tenant";
    public static final String MARKER_ISOLATION = "skyeye_isolation";

    private static final Pattern MARKER_PATTERN = Pattern.compile(
        "\\[" + MARKER_TENANT + "=(.*?)\\]\\[" + MARKER_ISOLATION + "=(.*?)\\]");

    private KnowledgeTenantFilterHelper() {
    }

    public static String resolveIsolationKey(String raw) {
        if (StrUtil.isBlank(raw)) {
            return TenantEnum.STRONG_ISOLATION.getKey();
        }
        for (TenantEnum item : TenantEnum.values()) {
            if (item.getKey().equals(raw) || item.name().equalsIgnoreCase(raw) || item.getValue().equals(raw)) {
                return item.getKey();
            }
        }
        return TenantEnum.STRONG_ISOLATION.getKey();
    }

    public static TenantEnum resolveIsolation(String raw) {
        String key = resolveIsolationKey(raw);
        for (TenantEnum item : TenantEnum.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return TenantEnum.STRONG_ISOLATION;
    }

    /**
     * 强制隔离 / 弱隔离需要源表租户字段；不做隔离、仅平台不强制。
     */
    public static boolean needTenantColumn(String isolationKey) {
        TenantEnum type = resolveIsolation(isolationKey);
        return type == TenantEnum.STRONG_ISOLATION || type == TenantEnum.WEAK_ISOLATION;
    }

    /**
     * 写入知识库正文的机器可读标记（便于问答后过滤）。
     */
    public static String buildMarkers(String tenantId, String isolationKey) {
        return "[" + MARKER_TENANT + "=" + StrUtil.blankToDefault(tenantId, "") + "]["
            + MARKER_ISOLATION + "=" + resolveIsolationKey(isolationKey) + "]";
    }

    /**
     * 开启租户时，按块过滤知识库检索结果；未开启或无当前租户则原样返回。
     */
    public static String filterKnowledgeText(String knowledgeText, boolean tenantEnable, String currentTenantId) {
        if (!tenantEnable || StrUtil.isBlank(currentTenantId) || StrUtil.isBlank(knowledgeText)) {
            return knowledgeText;
        }
        List<String> chunks = splitChunks(knowledgeText);
        if (chunks.isEmpty()) {
            return knowledgeText;
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (String chunk : chunks) {
            if (!allowChunk(chunk, currentTenantId)) {
                continue;
            }
            sb.append(++index).append(". ").append(stripLeadingIndex(chunk).trim()).append("\n\n");
        }
        return sb.toString().trim();
    }

    public static boolean allowChunk(String chunk, String currentTenantId) {
        Matcher matcher = MARKER_PATTERN.matcher(StrUtil.blankToDefault(chunk, ""));
        if (!matcher.find()) {
            // 旧数据无标记：弱兼容，仍返回（避免全空）
            return true;
        }
        String rowTenant = StrUtil.blankToDefault(matcher.group(1), "");
        TenantEnum isolation = resolveIsolation(matcher.group(2));
        switch (isolation) {
            case NO_ISOLATION:
                return true;
            case STRONG_ISOLATION:
                return StrUtil.equals(currentTenantId, rowTenant);
            case WEAK_ISOLATION:
                return StrUtil.equals(currentTenantId, rowTenant)
                    || StrUtil.isBlank(rowTenant)
                    || TenantTypeEnum.isPlatform(rowTenant);
            case PLATE:
                return TenantTypeEnum.isPlatform(currentTenantId);
            default:
                return StrUtil.equals(currentTenantId, rowTenant);
        }
    }

    private static List<String> splitChunks(String text) {
        List<String> chunks = new ArrayList<>();
        String normalized = text.replace("\r\n", "\n").trim();
        // 优先按检索结果序号切分：1. xxx\n\n2. xxx
        Pattern numbered = Pattern.compile("(?m)^\\d+\\.\\s+");
        Matcher matcher = numbered.matcher(normalized);
        List<Integer> starts = new ArrayList<>();
        while (matcher.find()) {
            starts.add(matcher.start());
        }
        if (starts.size() >= 2) {
            for (int i = 0; i < starts.size(); i++) {
                int from = starts.get(i);
                int to = i + 1 < starts.size() ? starts.get(i + 1) : normalized.length();
                String part = normalized.substring(from, to).trim();
                if (StrUtil.isNotBlank(part)) {
                    chunks.add(part);
                }
            }
            return chunks;
        }
        // 回退：按同步分隔符 ---
        String[] parts = normalized.split("\\n---\\n|\\n---\\s*");
        for (String part : parts) {
            if (StrUtil.isNotBlank(part)) {
                chunks.add(part.trim());
            }
        }
        if (chunks.isEmpty() && StrUtil.isNotBlank(normalized)) {
            chunks.add(normalized);
        }
        return chunks;
    }

    private static String stripLeadingIndex(String chunk) {
        return chunk.replaceFirst("^\\d+\\.\\s+", "");
    }
}
