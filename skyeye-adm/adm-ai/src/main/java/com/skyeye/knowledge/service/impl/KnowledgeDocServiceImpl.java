/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.knowledge.dao.KnowledgeDocDao;
import com.skyeye.knowledge.entity.KnowledgeDoc;
import com.skyeye.knowledge.entity.KnowledgeSegment;
import com.skyeye.knowledge.service.KnowledgeDocService;
import com.skyeye.knowledge.service.KnowledgeSegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "AI知识库文档", groupName = "AI知识库", allowDynamicAttrKey = false)
public class KnowledgeDocServiceImpl extends SkyeyeBusinessServiceImpl<KnowledgeDocDao, KnowledgeDoc> implements KnowledgeDocService {

    private static final int CONTENT_LIMIT = 800;

    @Autowired
    private KnowledgeSegmentService knowledgeSegmentService;

    @Override
    public void deleteByKnowledgeId(String knowledgeId) {
        knowledgeSegmentService.deleteByKnowledgeId(knowledgeId);
        QueryWrapper<KnowledgeDoc> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeDoc::getKnowledgeId), knowledgeId);
        remove(queryWrapper);
    }

    @Override
    public void deleteByKnowledgeAndTable(String knowledgeId, String sourceTable) {
        QueryWrapper<KnowledgeDoc> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeDoc::getKnowledgeId), knowledgeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeDoc::getSourceTable), sourceTable);
        List<KnowledgeDoc> docs = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(docs)) {
            List<String> docIds = new ArrayList<>();
            for (KnowledgeDoc doc : docs) {
                docIds.add(doc.getId());
            }
            knowledgeSegmentService.deleteByDocIds(docIds);
        }
        remove(queryWrapper);
    }

    @Override
    public KnowledgeDoc selectBySource(String knowledgeId, String sourceTable, String sourceId) {
        QueryWrapper<KnowledgeDoc> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeDoc::getKnowledgeId), knowledgeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeDoc::getSourceTable), sourceTable);
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeDoc::getSourceId), sourceId);
        queryWrapper.last("LIMIT 1");
        return getOne(queryWrapper, false);
    }

    @Override
    public String searchContext(String knowledgeId, String queryText, int topN) {
        if (StrUtil.isBlank(knowledgeId) || StrUtil.isBlank(queryText)) {
            return StrUtil.EMPTY;
        }
        String keyword = queryText.length() > 40 ? queryText.substring(0, 40) : queryText;
        List<KnowledgeSegment> segments = knowledgeSegmentService.search(knowledgeId, keyword, topN);
        if (CollectionUtil.isEmpty(segments)) {
            return StrUtil.EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        int index = 1;
        for (KnowledgeSegment segment : segments) {
            String content = StrUtil.blankToDefault(segment.getContent(), StrUtil.EMPTY);
            if (content.length() > CONTENT_LIMIT) {
                content = content.substring(0, CONTENT_LIMIT);
            }
            builder.append(index++).append(". ")
                .append(StrUtil.blankToDefault(segment.getDocName(), "未命名"))
                .append('\n')
                .append(content)
                .append("\n\n");
        }
        return builder.toString().trim();
    }

    @Override
    public int saveSyncedRows(String knowledgeId, String sourceTable, List<Map<String, Object>> rows,
                              String idField, String titleField, List<String> contentFields,
                              boolean fullSync, boolean clearBefore) {
        if (fullSync && clearBefore) {
            deleteByKnowledgeAndTable(knowledgeId, sourceTable);
        }
        if (CollectionUtil.isEmpty(rows)) {
            return 0;
        }
        int count = 0;
        for (Map<String, Object> row : rows) {
            String sourceId = valueOf(row.get(idField));
            if (StrUtil.isBlank(sourceId)) {
                continue;
            }
            String title = StrUtil.blankToDefault(valueOf(row.get(titleField)), sourceId);
            String content = buildContent(row, contentFields);
            if (StrUtil.isBlank(content)) {
                continue;
            }
            KnowledgeDoc doc = fullSync ? null : selectBySource(knowledgeId, sourceTable, sourceId);
            if (doc == null || StrUtil.isBlank(doc.getId())) {
                doc = new KnowledgeDoc();
                doc.setKnowledgeId(knowledgeId);
                doc.setSourceTable(sourceTable);
                doc.setSourceId(sourceId);
                doc.setType("sync");
                doc.setStatus("complete");
                doc.setTitle(title);
                doc.setContent(content);
                doc.setMetadata(JSONUtil.toJsonStr(row));
                String docId = createEntity(doc, StrUtil.EMPTY);
                knowledgeSegmentService.saveForDoc(knowledgeId, docId, title, content);
            } else {
                knowledgeSegmentService.deleteByDocIds(java.util.Collections.singletonList(doc.getId()));
                doc.setTitle(title);
                doc.setContent(content);
                doc.setStatus("complete");
                doc.setMetadata(JSONUtil.toJsonStr(row));
                updateEntity(doc, StrUtil.EMPTY);
                knowledgeSegmentService.saveForDoc(knowledgeId, doc.getId(), title, content);
            }
            count++;
        }
        return count;
    }

    private String buildContent(Map<String, Object> row, List<String> contentFields) {
        StringBuilder builder = new StringBuilder();
        for (String field : contentFields) {
            String value = valueOf(row.get(field));
            if (StrUtil.isBlank(value)) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(field).append(": ").append(value);
        }
        return builder.toString();
    }

    private String valueOf(Object value) {
        return value == null ? StrUtil.EMPTY : String.valueOf(value);
    }

}
