package com.skyeye.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.skyeye.knowledge.classenum.KnowledgeDocStatusEnum;
import com.skyeye.knowledge.classenum.KnowledgeDocTypeEnum;
import com.skyeye.knowledge.dao.KnowledgeDocDao;
import com.skyeye.knowledge.embedding.EmbeddingConsts;
import com.skyeye.knowledge.embedding.EmbeddingHandler;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.entity.KnowledgeDoc;
import com.skyeye.knowledge.exception.CustomException;
import com.skyeye.knowledge.service.KnowledgeDocService;
import com.skyeye.knowledge.service.KnowledgeService;
import com.skyeye.knowledge.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * AI知识库文档服务
 */
@Service
public class KnowledgeDocServiceImpl implements KnowledgeDocService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeDocServiceImpl.class);
    private static final ExecutorService BUILD_EXECUTOR = Executors.newFixedThreadPool(4);
    private static final long BUILDING_TIMEOUT_MS = 5 * 60 * 1000L;
    private static final String LOCAL_USER = "local";

    @Autowired
    private KnowledgeDocDao knowledgeDocDao;

    @Autowired
    @Lazy
    private KnowledgeService knowledgeService;

    @Autowired
    private EmbeddingHandler embeddingHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeDoc editDocument(KnowledgeDoc doc) {
        if (!StringUtils.hasText(doc.getKnowledgeId())) {
            throw new CustomException("知识库不能为空");
        }
        if (!StringUtils.hasText(doc.getTitle())) {
            throw new CustomException("文档标题不能为空");
        }
        if (!StringUtils.hasText(doc.getType())) {
            throw new CustomException("文档类型不能为空");
        }
        if (KnowledgeDocTypeEnum.TEXT.getKey().equals(doc.getType()) && !StringUtils.hasText(doc.getContent())) {
            throw new CustomException("文档内容不能为空");
        }
        Knowledge knowledge = knowledgeService.selectById(doc.getKnowledgeId());
        if (!StringUtils.hasText(knowledge.getEmbedId())) {
            throw new CustomException("请先为知识库配置向量模型");
        }

        doc.setStatus(KnowledgeDocStatusEnum.DRAFT.getKey());
        String now = IdUtil.now();
        if (!StringUtils.hasText(doc.getId())) {
            doc.setId(IdUtil.uuid());
            doc.setCreateId(LOCAL_USER);
            doc.setCreateTime(now);
            doc.setLastUpdateId(LOCAL_USER);
            doc.setLastUpdateTime(now);
            knowledgeDocDao.insert(doc);
        } else {
            KnowledgeDoc old = knowledgeDocDao.selectById(doc.getId());
            if (old == null) {
                throw new CustomException("文档不存在: " + doc.getId());
            }
            doc.setCreateId(old.getCreateId());
            doc.setCreateTime(old.getCreateTime());
            doc.setLastUpdateId(LOCAL_USER);
            doc.setLastUpdateTime(now);
            knowledgeDocDao.updateById(doc);
        }
        rebuildDocumentByIds(Collections.singletonList(doc.getId()));
        return knowledgeDocDao.selectById(doc.getId());
    }

    @Override
    public KnowledgeDoc selectById(String id) {
        KnowledgeDoc doc = knowledgeDocDao.selectById(id);
        if (doc == null) {
            throw new CustomException("文档不存在: " + id);
        }
        return doc;
    }

    @Override
    public List<KnowledgeDoc> queryPageList(int page, int limit, String keyword, String knowledgeId) {
        Page<KnowledgeDoc> mpPage = new Page<>(Math.max(page, 1), Math.max(limit, 1));
        return knowledgeDocDao.selectPage(mpPage, buildQuery(keyword, knowledgeId)).getRecords();
    }

    @Override
    public long count(String keyword, String knowledgeId) {
        return knowledgeDocDao.selectCount(buildQuery(keyword, knowledgeId));
    }

    @Override
    public void rebuildDocument(String docIds) {
        if (!StringUtils.hasText(docIds)) {
            throw new CustomException("请选择要重建的文档");
        }
        List<String> idList = Arrays.stream(docIds.split(",")).filter(StringUtils::hasText).collect(Collectors.toList());
        rebuildDocumentByIds(idList);
    }

    @Override
    public void rebuildDocumentByKnowId(String knowId) {
        if (!StringUtils.hasText(knowId)) {
            throw new CustomException("知识库id不能为空");
        }
        List<KnowledgeDoc> docs = knowledgeDocDao.selectList(new LambdaQueryWrapper<KnowledgeDoc>()
            .eq(KnowledgeDoc::getKnowledgeId, knowId));
        if (CollectionUtils.isEmpty(docs)) {
            return;
        }
        rebuildDocumentByIds(docs.stream().map(KnowledgeDoc::getId).collect(Collectors.toList()));
    }

    private void rebuildDocumentByIds(List<String> docIdList) {
        if (CollectionUtils.isEmpty(docIdList)) {
            return;
        }
        List<KnowledgeDoc> docs = knowledgeDocDao.selectBatchIds(docIdList);
        if (CollectionUtils.isEmpty(docs)) {
            throw new CustomException("文档不存在");
        }
        long now = System.currentTimeMillis();
        List<KnowledgeDoc> needRebuild = new ArrayList<>();
        for (KnowledgeDoc doc : docs) {
            if (KnowledgeDocStatusEnum.BUILDING.getKey().equalsIgnoreCase(doc.getStatus())
                && StringUtils.hasText(doc.getLastUpdateTime())) {
                try {
                    Date lastUpdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(doc.getLastUpdateTime());
                    if (lastUpdate != null && (now - lastUpdate.getTime()) <= BUILDING_TIMEOUT_MS) {
                        continue;
                    }
                } catch (Exception ignored) {
                }
            }
            doc.setStatus(KnowledgeDocStatusEnum.BUILDING.getKey());
            doc.setLastUpdateTime(IdUtil.now());
            needRebuild.add(doc);
        }
        if (CollectionUtils.isEmpty(needRebuild)) {
            return;
        }
        for (KnowledgeDoc doc : needRebuild) {
            knowledgeDocDao.updateById(doc);
            CompletableFuture.runAsync(() -> buildDocument(doc), BUILD_EXECUTOR);
        }
    }

    private void buildDocument(KnowledgeDoc doc) {
        try {
            Knowledge knowledge = knowledgeService.selectById(doc.getKnowledgeId());
            Map<String, Object> metadata = embeddingHandler.embeddingDocument(knowledge, doc);
            if (metadata == null) {
                handleBuildFailed(doc, "向量化失败");
                return;
            }
            doc.setStatus(KnowledgeDocStatusEnum.COMPLETE.getKey());
            doc.setLastUpdateTime(IdUtil.now());
            knowledgeDocDao.updateById(doc);
            log.info("文档向量化成功 knowledgeId={}, docId={}", doc.getKnowledgeId(), doc.getId());
        } catch (Throwable t) {
            log.error("文档向量化失败 knowledgeId={}, docId={}", doc.getKnowledgeId(), doc.getId(), t);
            handleBuildFailed(doc, t.getMessage());
        }
    }

    private void handleBuildFailed(KnowledgeDoc doc, String failedReason) {
        doc.setStatus(KnowledgeDocStatusEnum.FAILED.getKey());
        try {
            ObjectNode metadata;
            if (StringUtils.hasText(doc.getMetadata())) {
                metadata = (ObjectNode) objectMapper.readTree(doc.getMetadata());
            } else {
                metadata = objectMapper.createObjectNode();
            }
            metadata.put("failedReason", failedReason == null ? "unknown" : failedReason);
            doc.setMetadata(objectMapper.writeValueAsString(metadata));
        } catch (Exception e) {
            doc.setMetadata("{\"failedReason\":\"" + (failedReason == null ? "unknown" : failedReason.replace("\"", "'")) + "\"}");
        }
        doc.setLastUpdateTime(IdUtil.now());
        knowledgeDocDao.updateById(doc);
    }

    @Override
    public void removeByKnowIds(List<String> knowIds) {
        if (CollectionUtils.isEmpty(knowIds)) {
            return;
        }
        for (String knowId : knowIds) {
            try {
                embeddingHandler.deleteEmbedDocsByKnowId(knowId);
            } catch (Exception e) {
                log.warn("删除知识库向量失败 knowId={}", knowId, e);
            }
        }
        knowledgeDocDao.delete(new LambdaQueryWrapper<KnowledgeDoc>().in(KnowledgeDoc::getKnowledgeId, knowIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByKnowId(String knowId) {
        if (!StringUtils.hasText(knowId)) {
            throw new CustomException("知识库id不能为空");
        }
        removeByKnowIds(Collections.singletonList(knowId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocByIds(String ids) {
        if (!StringUtils.hasText(ids)) {
            throw new CustomException("请选择要删除的文档");
        }
        List<String> idList = Arrays.stream(ids.split(",")).filter(StringUtils::hasText).collect(Collectors.toList());
        try {
            embeddingHandler.deleteEmbedDocsByDocIds(idList);
        } catch (Exception e) {
            log.warn("删除文档向量失败 ids={}", ids, e);
        }
        knowledgeDocDao.deleteBatchIds(idList);
    }

    @Override
    public List<Map<String, Object>> searchByKnowledge(List<String> knowIds, String queryText, Integer topNumber) {
        return searchByKnowledge(knowIds, queryText, topNumber, EmbeddingConsts.DEFAULT_SIMILARITY, LOCAL_USER);
    }

    @Override
    public List<Map<String, Object>> searchByKnowledge(List<String> knowIds, String queryText,
                                                     Integer topNumber, Double similarity, String currentUserId) {
        return embeddingHandler.embeddingSearch(knowIds, queryText, topNumber, similarity, currentUserId);
    }

    private LambdaQueryWrapper<KnowledgeDoc> buildQuery(String keyword, String knowledgeId) {
        LambdaQueryWrapper<KnowledgeDoc> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(knowledgeId)) {
            wrapper.eq(KnowledgeDoc::getKnowledgeId, knowledgeId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(KnowledgeDoc::getTitle, keyword).or().like(KnowledgeDoc::getContent, keyword));
        }
        wrapper.orderByDesc(KnowledgeDoc::getCreateTime);
        return wrapper;
    }
}
