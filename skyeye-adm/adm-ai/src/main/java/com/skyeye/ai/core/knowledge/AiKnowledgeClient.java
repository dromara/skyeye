/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.knowledge;

import cn.hutool.core.util.StrUtil;

/**
 * 各 AI 平台知识库统一客户端：上传文档 + 检索。
 */
public interface AiKnowledgeClient {

    /**
     * 上传文本文件到平台知识库。
     *
     * @param fileName 含后缀，如 tenant_20260825_xxxx.txt
     * @param content  文本正文
     * @return 平台文档 ID，可空
     */
    String uploadText(AiKnowledgeConfig config, String fileName, String content);

    /**
     * 上传文本；若平台支持 URL/TOS 整文件导入，可传入 fileUrl / tosPath。
     * platformDocName 为平台侧展示用文档名（如含日期目录信息），为空则用 fileName。
     */
    default String uploadText(AiKnowledgeConfig config, String fileName, String content,
                              String fileUrl, String tosPath, String platformDocName) {
        return uploadText(config, StrUtil.blankToDefault(platformDocName, fileName), content);
    }

    default String uploadText(AiKnowledgeConfig config, String fileName, String content,
                              String fileUrl, String tosPath) {
        return uploadText(config, fileName, content);
    }

    /**
     * 上传原始文件（PDF/Word 等）到平台知识库。默认按 URL/TOS 走 uploadText。
     */
    default String uploadFile(AiKnowledgeConfig config, String fileName, String fileUrl, String tosPath) {
        return uploadText(config, fileName, StrUtil.EMPTY, fileUrl, tosPath, fileName);
    }

    /**
     * 按平台文档 ID 删除。不支持时忽略。
     */
    default void deleteDoc(AiKnowledgeConfig config, String docId) {
    }

    /**
     * 平台侧检索，返回可拼进 prompt 的文本；不支持时返回空。
     */
    String search(AiKnowledgeConfig config, String query, int topN);

    /**
     * 是否依赖应用控制台绑库（如通义百炼应用），对话无需本地拼 RAG。
     */
    default boolean useNativeAppKnowledge() {
        return false;
    }

}
