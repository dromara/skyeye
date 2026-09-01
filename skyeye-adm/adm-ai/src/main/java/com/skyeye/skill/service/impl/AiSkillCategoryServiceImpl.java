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
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.skill.dao.AiSkillCategoryDao;
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

@Service
@SkyeyeService(name = "AI技能分类", groupName = "AI技能", allowDynamicAttrKey = false)
public class AiSkillCategoryServiceImpl extends SkyeyeBusinessServiceImpl<AiSkillCategoryDao, AiSkillCategory>
    implements AiSkillCategoryService {

    @Autowired
    @Lazy
    private AiSkillService aiSkillService;

    @Autowired
    @Lazy
    private AiSkillSuiteService aiSkillSuiteService;

    @Override
    public void validatorEntity(AiSkillCategory entity) {
        super.validatorEntity(entity);
        if (StrUtil.isBlank(entity.getOddNumber())) {
            entity.setOddNumber("cate" + ToolUtil.getSurFaceId().substring(0, 8));
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(EnableEnum.ENABLE_USING.getKey());
        }
        if (entity.getOrderBy() == null) {
            entity.setOrderBy(100);
        }
    }

    @Override
    protected QueryWrapper<AiSkillCategory> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AiSkillCategory> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AiSkillCategory::getOrderBy));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AiSkillCategory::getCreateTime));
        return queryWrapper;
    }

    @Override
    public void deletePostpose(String id) {
        List<AiSkill> skills = aiSkillService.queryByCategoryId(id);
        if (skills != null && !skills.isEmpty()) {
            throw new CustomException("分类下仍有技能，请先解绑或调整分类");
        }
        List<AiSkillSuite> suites = aiSkillSuiteService.queryByCategoryId(id);
        if (suites != null && !suites.isEmpty()) {
            throw new CustomException("分类下仍有套件，请先解绑或调整分类");
        }
    }

    @Override
    public List<AiSkillCategory> queryEnabledList() {
        QueryWrapper<AiSkillCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkillCategory::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AiSkillCategory::getOrderBy));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AiSkillCategory::getCreateTime));
        return list(queryWrapper);
    }
}
