/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.knowledge;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.knowledge.util.KnowledgeTenantFilterHelper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 知识库上传文件命名与正文拼装。
 * <p>
 * 表分片 / 文件使用稳定 doc_id（不含日期、UUID），平台侧重复上传同一 ID 会覆盖，避免全量/增量反复同步产生重复文档。
 */
public final class AiKnowledgeUploadHelper {

    private static final String OBJECT_DIR_PREFIX = "knowledge/";

    private AiKnowledgeUploadHelper() {
    }

    /**
     * 存储路径前缀：knowledge/{知识库id}/{yyyy-MM-dd}/
     * （仅 TOS 临时对象路径，导入后可删除；平台文档 ID 不走这里）
     */
    public static String buildObjectDir(String knowledgeId) {
        String kbId = sanitizeToken(knowledgeId, 64);
        String date = DateUtil.getYmdTimeAndToString();
        return OBJECT_DIR_PREFIX + kbId + "/" + date + "/";
    }

    /**
     * 表分片 TOS/本地文件名，与平台 doc_id 一致，便于覆盖。
     */
    public static String buildTableFileName(String knowledgeId, String tableName, int partIndex) {
        return buildTableDocId(knowledgeId, tableName, partIndex) + ".txt";
    }

    /**
     * 平台展示名（豆包 doc_name 不允许含 /）。
     */
    public static String buildTableDocName(String knowledgeId, String tableName, int partIndex) {
        return buildTableFileName(knowledgeId, tableName, partIndex);
    }

    /**
     * 稳定文档 ID：t_{知识库}_{表}_p0001。同一表同一分片反复同步会覆盖。
     */
    public static String buildTableDocId(String knowledgeId, String tableName, int partIndex) {
        return buildTableDocPrefix(knowledgeId, tableName) + String.format("%04d", Math.max(partIndex, 1));
    }

    public static String buildTableDocPrefix(String knowledgeId, String tableName) {
        // 全量覆盖时按此前缀识别本表分片：t_{kb}_{table}_p
        return "t_" + sanitizeToken(knowledgeId, 24) + "_" + sanitizeToken(tableName, 40) + "_p";
    }

    /**
     * 知识库文件稳定文档 ID：f_{文件记录id}。同一文件反复同步覆盖，不再追加 UUID。
     */
    public static String buildFileDocId(String knowledgeId, String fileId) {
        return "f_" + sanitizeToken(fileId, 80);
    }

    /**
     * 解析库里保存的平台分片文档 ID（逗号分隔）。
     */
    public static List<String> splitDocIds(String raw) {
        List<String> ids = new ArrayList<>();
        if (StrUtil.isBlank(raw)) {
            return ids;
        }
        for (String item : raw.split(",")) {
            String id = item.trim();
            if (StrUtil.isNotBlank(id)) {
                ids.add(id);
            }
        }
        return ids;
    }

    /**
     * 去重后拼回逗号分隔，写入 part_doc_ids。
     */
    public static String joinDocIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return StrUtil.EMPTY;
        }
        Set<String> unique = new LinkedHashSet<>();
        for (String id : ids) {
            if (StrUtil.isNotBlank(id)) {
                unique.add(id.trim());
            }
        }
        return String.join(",", unique);
    }

    /**
     * 平台 doc_id 只允许字母数字下划线，并截断到 max 长度。
     */
    public static String sanitizeToken(String raw, int max) {
        String id = StrUtil.blankToDefault(raw, "x").replaceAll("[^A-Za-z0-9_]", "_");
        if (StrUtil.isBlank(id)) {
            id = "x";
        }
        if (id.length() > max) {
            id = id.substring(0, max);
        }
        return id;
    }

    /**
     * 按文件名推断平台文档类型（豆包 doc_type）。
     */
    public static String resolveDocType(String fileName) {
        String ext = StrUtil.subAfter(StrUtil.blankToDefault(fileName, ""), ".", true);
        if (StrUtil.isBlank(ext)) {
            return "txt";
        }
        ext = ext.toLowerCase();
        if ("md".equals(ext) || "markdown".equals(ext)) {
            return "markdown";
        }
        if ("htm".equals(ext)) {
            return "html";
        }
        return ext;
    }

    public static String buildRowBlock(String tableName, String tableRemark, String idField, String titleField,
                                      Map<String, Object> row, List<String> contentFields,
                                      Map<String, String> fieldRemarks) {
        return buildRowBlock(tableName, tableRemark, idField, titleField, row, contentFields, fieldRemarks,
            null, null);
    }

    /**
     * @param tenantField      源表租户字段名，写入正文便于问答按租户过滤
     * @param tenantIsolation  表数据隔离类型（TenantEnum.key）
     */
    public static String buildRowBlock(String tableName, String tableRemark, String idField, String titleField,
                                      Map<String, Object> row, List<String> contentFields,
                                      Map<String, String> fieldRemarks, String tenantField, String tenantIsolation) {
        StringBuilder sb = new StringBuilder();
        sb.append("表: ").append(tableName);
        if (StrUtil.isNotBlank(tableRemark)) {
            sb.append(" (").append(tableRemark).append(')');
        }
        sb.append('\n');
        String rowTenantId = StrUtil.EMPTY;
        if (StrUtil.isNotBlank(tenantField) && row != null && row.get(tenantField) != null) {
            rowTenantId = String.valueOf(row.get(tenantField));
        }
        String isolationKey = StrUtil.blankToDefault(tenantIsolation, "strongIsolation");
        sb.append(KnowledgeTenantFilterHelper.buildMarkers(rowTenantId, isolationKey)).append('\n');
        if (StrUtil.isNotBlank(tenantField)) {
            appendFieldLine(sb, "租户ID", tenantField, rowTenantId, fieldRemarks);
        }
        appendFieldLine(sb, "数据隔离", "tenant_isolation", isolationKey, fieldRemarks);
        appendFieldLine(sb, "主键", idField, row == null ? null : row.get(idField), fieldRemarks);
        if (StrUtil.isNotBlank(titleField) && row != null && row.get(titleField) != null) {
            appendFieldLine(sb, "标题", titleField, row.get(titleField), fieldRemarks);
        }
        for (String field : contentFields) {
            Object val = row == null ? null : row.get(field);
            if (val == null || StrUtil.isBlank(String.valueOf(val))) {
                continue;
            }
            appendFieldLine(sb, null, field, val, fieldRemarks);
        }
        sb.append("---\n");
        return sb.toString();
    }

    private static void appendFieldLine(StringBuilder sb, String roleLabel, String field, Object value,
                                        Map<String, String> fieldRemarks) {
        if (value == null || StrUtil.isBlank(String.valueOf(value))) {
            return;
        }
        String remark = fieldRemarks == null ? null : fieldRemarks.get(field);
        if (StrUtil.isNotBlank(roleLabel)) {
            sb.append(roleLabel).append('(').append(formatFieldName(field, remark)).append("): ")
                .append(value).append('\n');
            return;
        }
        sb.append(formatFieldName(field, remark)).append(": ").append(value).append('\n');
    }

    private static String formatFieldName(String field, String remark) {
        if (StrUtil.isBlank(remark)) {
            return field;
        }
        return field + " (" + remark + ")";
    }

}
