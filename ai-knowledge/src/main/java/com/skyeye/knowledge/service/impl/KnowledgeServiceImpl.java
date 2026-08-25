package com.skyeye.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skyeye.knowledge.classenum.AiPlatformEnum;
import com.skyeye.knowledge.classenum.KnowledgeStatusEnum;
import com.skyeye.knowledge.classenum.KnowledgeTypeEnum;
import com.skyeye.knowledge.dao.KnowledgeDao;
import com.skyeye.knowledge.entity.EmbedModel;
import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.exception.CustomException;
import com.skyeye.knowledge.service.EmbedModelService;
import com.skyeye.knowledge.service.KnowledgeDocService;
import com.skyeye.knowledge.service.KnowledgeService;
import com.skyeye.knowledge.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI知识库服务
 */
@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private static final String LOCAL_USER = "local";

    @Autowired
    private KnowledgeDao knowledgeDao;

    @Autowired
    private EmbedModelService embedModelService;

    @Autowired
    @Lazy
    private KnowledgeDocService knowledgeDocService;

    @Override
    public Knowledge saveOrUpdate(Knowledge knowledge) {
        if (!StringUtils.hasText(knowledge.getName())) {
            throw new CustomException("知识库名称不能为空");
        }
        if (!StringUtils.hasText(knowledge.getEmbedId())) {
            throw new CustomException("向量模型配置不能为空");
        }
        EmbedModel embedModel = embedModelService.selectById(knowledge.getEmbedId());
        if (AiPlatformEnum.XUN_FEI.getKey().equalsIgnoreCase(embedModel.getPlatform())) {
            throw new CustomException("讯飞平台暂不支持向量化，请改用通义或文心配置");
        }
        if (!StringUtils.hasText(knowledge.getStatus())) {
            knowledge.setStatus(KnowledgeStatusEnum.ENABLE.getKey());
        }
        if (!StringUtils.hasText(knowledge.getType())) {
            knowledge.setType(KnowledgeTypeEnum.KNOWLEDGE.getKey());
        }

        String now = IdUtil.now();
        if (!StringUtils.hasText(knowledge.getId())) {
            knowledge.setId(IdUtil.uuid());
            knowledge.setCreateId(LOCAL_USER);
            knowledge.setCreateTime(now);
            knowledge.setLastUpdateId(LOCAL_USER);
            knowledge.setLastUpdateTime(now);
            knowledgeDao.insert(knowledge);
        } else {
            Knowledge old = knowledgeDao.selectById(knowledge.getId());
            if (old == null) {
                throw new CustomException("知识库不存在: " + knowledge.getId());
            }
            String oldEmbedId = old.getEmbedId();
            knowledge.setCreateId(old.getCreateId());
            knowledge.setCreateTime(old.getCreateTime());
            knowledge.setLastUpdateId(LOCAL_USER);
            knowledge.setLastUpdateTime(now);
            knowledgeDao.updateById(knowledge);
            if (StringUtils.hasText(oldEmbedId) && !oldEmbedId.equalsIgnoreCase(knowledge.getEmbedId())) {
                knowledgeDocService.rebuildDocumentByKnowId(knowledge.getId());
            }
        }
        return selectById(knowledge.getId());
    }

    @Override
    public Knowledge selectById(String id) {
        Knowledge knowledge = knowledgeDao.selectById(id);
        if (knowledge == null) {
            throw new CustomException("知识库不存在: " + id);
        }
        try {
            knowledge.setEmbedMation(embedModelService.selectById(knowledge.getEmbedId()));
        } catch (Exception ignored) {
            // embed 可能已删
        }
        return knowledge;
    }

    @Override
    public List<Knowledge> queryPageList(int page, int limit, String keyword) {
        Page<Knowledge> mpPage = new Page<>(Math.max(page, 1), Math.max(limit, 1));
        List<Knowledge> list = knowledgeDao.selectPage(mpPage, buildQuery(keyword)).getRecords();
        fillEmbed(list);
        return list;
    }

    @Override
    public long count(String keyword) {
        return knowledgeDao.selectCount(buildQuery(keyword));
    }

    @Override
    public List<Knowledge> queryList() {
        List<Knowledge> list = knowledgeDao.selectList(new LambdaQueryWrapper<Knowledge>()
            .eq(Knowledge::getStatus, KnowledgeStatusEnum.ENABLE.getKey())
            .orderByDesc(Knowledge::getCreateTime));
        fillEmbed(list);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        knowledgeDocService.removeByKnowIds(Collections.singletonList(id));
        if (knowledgeDao.deleteById(id) <= 0) {
            throw new CustomException("知识库不存在: " + id);
        }
    }

    @Override
    public void rebuildKnowledge(String knowIds) {
        if (!StringUtils.hasText(knowIds)) {
            throw new CustomException("请选择要重建的知识库");
        }
        Arrays.stream(knowIds.split(","))
            .filter(StringUtils::hasText)
            .forEach(knowledgeDocService::rebuildDocumentByKnowId);
    }

    @Override
    public List<Map<String, Object>> hitTest(String knowId, String queryText, Integer topNumber, Double similarity) {
        if (!StringUtils.hasText(knowId) || !StringUtils.hasText(queryText)) {
            throw new CustomException("knowId/queryText不能为空");
        }
        return knowledgeDocService.searchByKnowledge(
            Collections.singletonList(knowId), queryText, topNumber, similarity, LOCAL_USER);
    }

    @Override
    public List<Map<String, Object>> embeddingSearch(String knowIds, String queryText,
                                                       Integer topNumber, Double similarity) {
        if (!StringUtils.hasText(knowIds) || !StringUtils.hasText(queryText)) {
            throw new CustomException("knowIds/queryText不能为空");
        }
        List<String> idList = Arrays.stream(knowIds.split(","))
            .filter(StringUtils::hasText)
            .collect(Collectors.toList());
        return knowledgeDocService.searchByKnowledge(idList, queryText, topNumber, similarity, LOCAL_USER);
    }

    private void fillEmbed(List<Knowledge> list) {
        for (Knowledge knowledge : list) {
            try {
                knowledge.setEmbedMation(embedModelService.selectById(knowledge.getEmbedId()));
            } catch (Exception ignored) {
            }
        }
    }

    private LambdaQueryWrapper<Knowledge> buildQuery(String keyword) {
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Knowledge::getName, keyword).or().like(Knowledge::getDescr, keyword));
        }
        wrapper.orderByDesc(Knowledge::getCreateTime);
        return wrapper;
    }
}
