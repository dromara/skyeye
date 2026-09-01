/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.skill.dao.AiSkillSuiteDao;
import com.skyeye.skill.entity.AiSkill;
import com.skyeye.skill.entity.AiSkillCategory;
import com.skyeye.skill.entity.AiSkillSuite;
import com.skyeye.skill.service.AiSkillCategoryService;
import com.skyeye.skill.service.AiSkillService;
import com.skyeye.skill.service.AiSkillSuiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "AI技能套件", groupName = "AI技能", allowDynamicAttrKey = false)
public class AiSkillSuiteServiceImpl extends SkyeyeBusinessServiceImpl<AiSkillSuiteDao, AiSkillSuite> implements AiSkillSuiteService {

    @Autowired
    @Lazy
    private AiSkillService aiSkillService;

    @Autowired
    @Lazy
    private AiSkillCategoryService aiSkillCategoryService;

    @Override
    public void validatorEntity(AiSkillSuite entity) {
        super.validatorEntity(entity);
        if (StrUtil.isBlank(entity.getOddNumber())) {
            entity.setOddNumber("suite" + ToolUtil.getSurFaceId().substring(0, 8));
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(EnableEnum.ENABLE_USING.getKey());
        }
        if (entity.getOrderBy() == null) {
            entity.setOrderBy(100);
        }
        if (StrUtil.isNotBlank(entity.getCategoryId())) {
            AiSkillCategory category = aiSkillCategoryService.selectById(entity.getCategoryId());
            if (category == null || StrUtil.isBlank(category.getId())) {
                throw new CustomException("所属分类不存在");
            }
        }
    }

    @Override
    public AiSkillSuite selectById(String id) {
        AiSkillSuite suite = super.selectById(id);
        if (suite != null && StrUtil.isNotBlank(suite.getCategoryId())) {
            aiSkillCategoryService.setDataMation(suite, AiSkillSuite::getCategoryId);
        }
        return suite;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        aiSkillCategoryService.setMationForMap(beans, "categoryId", "categoryMation");
        return beans;
    }

    @Override
    protected QueryWrapper<AiSkillSuite> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AiSkillSuite> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AiSkillSuite::getOrderBy));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AiSkillSuite::getCreateTime));
        return queryWrapper;
    }

    @Override
    public void deletePostpose(String id) {
        List<AiSkill> skills = aiSkillService.queryBySuiteId(id);
        if (skills != null && !skills.isEmpty()) {
            throw new CustomException("套件下仍有技能，请先解绑或删除技能");
        }
    }

    @Override
    public List<AiSkillSuite> queryEnabledList() {
        QueryWrapper<AiSkillSuite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkillSuite::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AiSkillSuite::getOrderBy));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AiSkillSuite::getCreateTime));
        List<AiSkillSuite> list = list(queryWrapper);
        if (list != null && !list.isEmpty()) {
            aiSkillCategoryService.setDataMation(list, AiSkillSuite::getCategoryId);
        }
        return list;
    }

    @Override
    public List<AiSkillSuite> queryByCategoryId(String categoryId) {
        if (StrUtil.isBlank(categoryId)) {
            return java.util.Collections.emptyList();
        }
        QueryWrapper<AiSkillSuite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkillSuite::getCategoryId), categoryId);
        return list(queryWrapper);
    }
}
