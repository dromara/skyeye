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
import com.skyeye.skill.dao.AiSkillSuiteDao;
import com.skyeye.skill.entity.AiSkill;
import com.skyeye.skill.entity.AiSkillSuite;
import com.skyeye.skill.service.AiSkillService;
import com.skyeye.skill.service.AiSkillSuiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "AI技能套件", groupName = "AI技能", allowDynamicAttrKey = false)
public class AiSkillSuiteServiceImpl extends SkyeyeBusinessServiceImpl<AiSkillSuiteDao, AiSkillSuite> implements AiSkillSuiteService {

    @Autowired
    @Lazy
    private AiSkillService aiSkillService;

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
        return list(queryWrapper);
    }
}
