/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.knowledge;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.util.DateUtil;

import java.util.List;
import java.util.Map;

/**
 * 知识库上传文件命名与正文拼装。
 */
public final class AiKnowledgeUploadHelper {

    private static final String OBJECT_DIR_PREFIX = "knowledge/";

    private AiKnowledgeUploadHelper() {
    }

    /**
     * 存储路径前缀：knowledge/{知识库id}/{yyyy-MM-dd}/
     */
    public static String buildObjectDir(String knowledgeId) {
        String kbId = StrUtil.blankToDefault(knowledgeId, "unknown");
        kbId = kbId.replaceAll("[^A-Za-z0-9_-]", "_");
        String date = DateUtil.getYmdTimeAndToString();
        return OBJECT_DIR_PREFIX + kbId + "/" + date + "/";
    }

    /** 分片文件名（含日期，便于 TOS/平台侧识别） */
    public static String buildFileName(int partIndex) {
        String date = DateUtil.getYmdTimeAndToString();
        String seq = String.format("%04d", Math.max(partIndex, 1));
        return date + "_part_" + seq + "_" + IdUtil.fastSimpleUUID().substring(0, 8) + ".txt";
    }

    /**
     * 平台知识库展示用文档名：{知识库id}_{日期}_part_{序号}.txt
     * （豆包 doc_name 不允许含 /，目录信息用下划线表达）
     */
    public static String buildPlatformDocName(String knowledgeId, int partIndex) {
        String kbId = StrUtil.blankToDefault(knowledgeId, "unknown");
        kbId = kbId.replaceAll("[^A-Za-z0-9_-]", "_");
        if (kbId.length() > 32) {
            kbId = kbId.substring(0, 32);
        }
        String date = DateUtil.getYmdTimeAndToString();
        String seq = String.format("%04d", Math.max(partIndex, 1));
        return kbId + "_" + date + "_part_" + seq + ".txt";
    }

    public static String buildRowBlock(String tableName, String idField, String titleField,
                                      Map<String, Object> row, List<String> contentFields) {
        StringBuilder sb = new StringBuilder();
        sb.append("表: ").append(tableName).append('\n');
        if (row.get(idField) != null) {
            sb.append("主键: ").append(row.get(idField)).append('\n');
        }
        if (StrUtil.isNotBlank(titleField) && row.get(titleField) != null) {
            sb.append("标题: ").append(row.get(titleField)).append('\n');
        }
        for (String field : contentFields) {
            Object val = row.get(field);
            if (val == null || StrUtil.isBlank(String.valueOf(val))) {
                continue;
            }
            sb.append(field).append(": ").append(val).append('\n');
        }
        sb.append("---\n");
        return sb.toString();
    }

}
