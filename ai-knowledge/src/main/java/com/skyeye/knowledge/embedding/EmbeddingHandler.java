package com.skyeye.knowledge.embedding;

import com.skyeye.knowledge.classenum.KnowledgeDocTypeEnum;
import com.skyeye.knowledge.classenum.KnowledgeTypeEnum;
import com.skyeye.knowledge.embedding.client.EmbeddingClient;
import com.skyeye.knowledge.embedding.client.EmbeddingClientFactory;
import com.skyeye.knowledge.entity.EmbedModel;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.entity.KnowledgeDoc;
import com.skyeye.knowledge.entity.KnowledgeSegment;
import com.skyeye.knowledge.exception.CustomException;
import com.skyeye.knowledge.service.EmbedModelService;
import com.skyeye.knowledge.service.KnowledgeSegmentService;
import com.skyeye.knowledge.service.KnowledgeService;
import com.skyeye.knowledge.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识库向量化处理（MySQL 存向量 JSON + 余弦相似度检索）
 */
@Component
public class EmbeddingHandler {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingHandler.class);

    @Autowired
    @Lazy
    private KnowledgeService knowledgeService;

    @Autowired
    private EmbedModelService embedModelService;

    @Autowired
    private KnowledgeSegmentService knowledgeSegmentService;

    public Map<String, Object> embeddingDocument(Knowledge knowledge, KnowledgeDoc doc) {
        if (knowledge == null || !StringUtils.hasText(knowledge.getId())) {
            throw new CustomException("知识库不存在");
        }
        if (!StringUtils.hasText(knowledge.getEmbedId())) {
            throw new CustomException("请先为知识库配置向量模型");
        }
        if (doc == null) {
            throw new CustomException("文档不能为空");
        }
        String content = resolveContent(doc);
        if (!StringUtils.hasText(content)) {
            throw new CustomException("文档内容为空，无法向量化");
        }
        if (StringUtils.hasText(doc.getTitle())) {
            content = doc.getTitle() + "\n\n" + content;
        }

        EmbedModel embedModel = embedModelService.selectById(knowledge.getEmbedId());
        EmbeddingClient client = EmbeddingClientFactory.getClient(embedModel);
        DocumentSplitter splitter = DocumentSplitter.fromMetadata(doc.getMetadata(), knowledge.getMetadata());
        List<String> segments = splitter.split(content);
        if (CollectionUtils.isEmpty(segments)) {
            throw new CustomException("文档分段结果为空");
        }

        knowledgeSegmentService.deleteByDocIds(Collections.singletonList(doc.getId()));

        List<KnowledgeSegment> segmentEntities = new ArrayList<>();
        for (int i = 0; i < segments.size(); i += EmbeddingConsts.EMBED_BATCH_SIZE) {
            List<String> batch = segments.subList(i, Math.min(i + EmbeddingConsts.EMBED_BATCH_SIZE, segments.size()));
            List<float[]> vectors = client.embed(batch);
            for (int j = 0; j < batch.size(); j++) {
                float[] vector = vectors.get(j);
                KnowledgeSegment segment = new KnowledgeSegment();
                segment.setId(IdUtil.uuid());
                segment.setKnowledgeId(doc.getKnowledgeId());
                segment.setDocId(doc.getId());
                segment.setDocName(doc.getTitle());
                segment.setContent(batch.get(j));
                segment.setEmbedding(VectorUtils.toJson(vector));
                segment.setDimension(vector.length);
                segment.setSegmentIndex(i + j);
                segment.setCreateId(doc.getCreateId());
                segment.setCreateTime(doc.getCreateTime());
                segment.setLastUpdateId(doc.getCreateId());
                segment.setLastUpdateTime(IdUtil.now());
                segmentEntities.add(segment);
            }
        }
        knowledgeSegmentService.createEntities(segmentEntities);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(EmbeddingConsts.META_DOC_ID, doc.getId());
        metadata.put(EmbeddingConsts.META_KNOWLEDGE_ID, doc.getKnowledgeId());
        metadata.put(EmbeddingConsts.META_DOC_NAME, doc.getTitle());
        metadata.put("segmentCount", segmentEntities.size());
        log.info("文档向量化完成 knowledgeId={}, docId={}, segments={}",
            doc.getKnowledgeId(), doc.getId(), segmentEntities.size());
        return metadata;
    }

    private String resolveContent(KnowledgeDoc doc) {
        if (StringUtils.hasText(doc.getContent())) {
            return doc.getContent();
        }
        if (KnowledgeDocTypeEnum.FILE.getKey().equals(doc.getType())
            || KnowledgeDocTypeEnum.WEB.getKey().equals(doc.getType())) {
            throw new CustomException("文件/网页文档请先解析出文本内容后再向量化");
        }
        return doc.getContent();
    }

    public List<Map<String, Object>> embeddingSearch(List<String> knowIds, String queryText,
                                                     Integer topNumber, Double similarity, String currentUserId) {
        if (CollectionUtils.isEmpty(knowIds) || !StringUtils.hasText(queryText)) {
            return Collections.emptyList();
        }
        int limit = topNumber == null || topNumber <= 0 ? EmbeddingConsts.DEFAULT_TOP_NUMBER : topNumber;
        double minScore = similarity == null ? EmbeddingConsts.DEFAULT_SIMILARITY : similarity;

        List<Map<String, Object>> allHits = new ArrayList<>();
        for (String knowId : knowIds) {
            allHits.addAll(searchEmbedding(knowId, queryText, limit, minScore, currentUserId));
        }
        return allHits.stream()
            .sorted((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")))
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> searchEmbedding(String knowId, String queryText,
                                                     Integer topNumber, Double similarity, String currentUserId) {
        if (!StringUtils.hasText(knowId) || !StringUtils.hasText(queryText)) {
            return Collections.emptyList();
        }
        Knowledge knowledge = knowledgeService.selectById(knowId);
        if (!StringUtils.hasText(knowledge.getEmbedId())) {
            throw new CustomException("请先为知识库配置向量模型");
        }
        EmbedModel embedModel = embedModelService.selectById(knowledge.getEmbedId());
        EmbeddingClient client = EmbeddingClientFactory.getClient(embedModel);
        float[] queryVector = client.embed(Collections.singletonList(queryText)).get(0);

        int limit = topNumber == null || topNumber <= 0 ? EmbeddingConsts.DEFAULT_TOP_NUMBER : topNumber;
        double minScore = similarity == null ? EmbeddingConsts.DEFAULT_SIMILARITY : similarity;

        List<KnowledgeSegment> segments = knowledgeSegmentService.listByKnowIds(Collections.singletonList(knowId));
        if (CollectionUtils.isEmpty(segments)) {
            return Collections.emptyList();
        }

        boolean memoryMode = KnowledgeTypeEnum.MEMORY.getKey().equalsIgnoreCase(knowledge.getType());
        List<Map<String, Object>> scored = new ArrayList<>();
        for (KnowledgeSegment segment : segments) {
            if (memoryMode && StringUtils.hasText(currentUserId)
                && !currentUserId.equals(segment.getCreateId())) {
                continue;
            }
            float[] vector = VectorUtils.fromJson(segment.getEmbedding());
            double score = VectorUtils.cosineSimilarity(queryVector, vector);
            if (score < minScore) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("score", score);
            item.put("content", segment.getContent());
            item.put("chunk", segment.getSegmentIndex());
            item.put("docId", segment.getDocId());
            item.put(EmbeddingConsts.META_DOC_NAME, segment.getDocName());
            item.put(EmbeddingConsts.META_KNOWLEDGE_ID, segment.getKnowledgeId());
            item.put(EmbeddingConsts.META_CREATE_TIME, segment.getCreateTime());
            scored.add(item);
        }
        return scored.stream()
            .sorted((a, b) -> {
                if (memoryMode) {
                    String t1 = a.get(EmbeddingConsts.META_CREATE_TIME) == null
                        ? "" : String.valueOf(a.get(EmbeddingConsts.META_CREATE_TIME));
                    String t2 = b.get(EmbeddingConsts.META_CREATE_TIME) == null
                        ? "" : String.valueOf(b.get(EmbeddingConsts.META_CREATE_TIME));
                    int cmp = t2.compareTo(t1);
                    if (cmp != 0) {
                        return cmp;
                    }
                }
                return Double.compare((Double) b.get("score"), (Double) a.get("score"));
            })
            .limit(limit)
            .collect(Collectors.toList());
    }

    public void deleteEmbedDocsByKnowId(String knowId) {
        if (!StringUtils.hasText(knowId)) {
            return;
        }
        knowledgeSegmentService.deleteByKnowIds(Collections.singletonList(knowId));
    }

    public void deleteEmbedDocsByDocIds(List<String> docIds) {
        knowledgeSegmentService.deleteByDocIds(docIds);
    }
}
