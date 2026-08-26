/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.knowledge;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;

import java.util.List;
import java.util.Map;

/**
 * 知识库上传文件命名与正文拼装。
 */
public final class AiKnowledgeUploadHelper {

    private AiKnowledgeUploadHelper() {
    }

    /** 租户_日期_随机码.txt（豆包 doc_id 要求字母/下划线开头，租户为数字时加 t_ 前缀） */
    public static String buildFileName(String tenantId) {
        String tenant = StrUtil.blankToDefault(tenantId, TenantContext.getTenantId());
        if (StrUtil.isBlank(tenant)) {
            tenant = "default";
        }
        tenant = tenant.replaceAll("[^A-Za-z0-9_]", "_");
        if (!tenant.isEmpty()) {
            char first = tenant.charAt(0);
            if (!(Character.isLetter(first) || first == '_')) {
                tenant = "t_" + tenant;
            }
        }
        String date = DateUtil.getTimeAndToString().substring(0, 10).replace("-", "");
        return tenant + "_" + date + "_" + IdUtil.fastSimpleUUID().substring(0, 8) + ".txt";
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
