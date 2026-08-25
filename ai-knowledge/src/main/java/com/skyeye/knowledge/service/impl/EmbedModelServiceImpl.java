package com.skyeye.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skyeye.knowledge.classenum.AiPlatformEnum;
import com.skyeye.knowledge.dao.EmbedModelDao;
import com.skyeye.knowledge.entity.EmbedModel;
import com.skyeye.knowledge.exception.CustomException;
import com.skyeye.knowledge.service.EmbedModelService;
import com.skyeye.knowledge.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 向量模型配置服务
 */
@Service
public class EmbedModelServiceImpl implements EmbedModelService {

    private static final String LOCAL_USER = "local";
    private static final int ENABLED = 1;

    @Autowired
    private EmbedModelDao embedModelDao;

    @Override
    public EmbedModel saveOrUpdate(EmbedModel model) {
        if (!StringUtils.hasText(model.getName())) {
            throw new CustomException("名称不能为空");
        }
        if (!StringUtils.hasText(model.getPlatform())) {
            throw new CustomException("平台不能为空");
        }
        AiPlatformEnum.getName(model.getPlatform());
        if (AiPlatformEnum.XUN_FEI.getKey().equalsIgnoreCase(model.getPlatform())) {
            throw new CustomException("讯飞平台暂不支持向量化");
        }
        if (model.getEnabled() == null) {
            model.setEnabled(ENABLED);
        }
        String now = IdUtil.now();
        if (!StringUtils.hasText(model.getId())) {
            model.setId(IdUtil.uuid());
            model.setCreateId(LOCAL_USER);
            model.setCreateTime(now);
            model.setLastUpdateId(LOCAL_USER);
            model.setLastUpdateTime(now);
            embedModelDao.insert(model);
        } else {
            EmbedModel old = embedModelDao.selectById(model.getId());
            if (old == null) {
                throw new CustomException("向量模型不存在: " + model.getId());
            }
            model.setCreateId(old.getCreateId());
            model.setCreateTime(old.getCreateTime());
            model.setLastUpdateId(LOCAL_USER);
            model.setLastUpdateTime(now);
            embedModelDao.updateById(model);
        }
        return embedModelDao.selectById(model.getId());
    }

    @Override
    public EmbedModel selectById(String id) {
        EmbedModel model = embedModelDao.selectById(id);
        if (model == null) {
            throw new CustomException("向量模型不存在: " + id);
        }
        return model;
    }

    @Override
    public List<EmbedModel> queryPageList(int page, int limit, String keyword) {
        Page<EmbedModel> mpPage = new Page<>(Math.max(page, 1), Math.max(limit, 1));
        return embedModelDao.selectPage(mpPage, buildQuery(keyword)).getRecords();
    }

    @Override
    public long count(String keyword) {
        return embedModelDao.selectCount(buildQuery(keyword));
    }

    @Override
    public List<EmbedModel> queryList() {
        return embedModelDao.selectList(new LambdaQueryWrapper<EmbedModel>()
            .eq(EmbedModel::getEnabled, ENABLED)
            .orderByDesc(EmbedModel::getCreateTime));
    }

    @Override
    public void deleteById(String id) {
        if (embedModelDao.deleteById(id) <= 0) {
            throw new CustomException("向量模型不存在: " + id);
        }
    }

    private LambdaQueryWrapper<EmbedModel> buildQuery(String keyword) {
        LambdaQueryWrapper<EmbedModel> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(EmbedModel::getName, keyword).or().like(EmbedModel::getModel, keyword));
        }
        wrapper.orderByDesc(EmbedModel::getCreateTime);
        return wrapper;
    }
}
