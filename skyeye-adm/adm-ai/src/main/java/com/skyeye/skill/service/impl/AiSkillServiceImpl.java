/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.skill.dao.AiSkillDao;
import com.skyeye.skill.entity.AiSkill;
import com.skyeye.skill.entity.AiSkillSuite;
import com.skyeye.skill.service.AiSkillService;
import com.skyeye.skill.service.AiSkillSuiteService;
import com.skyeye.skill.util.AiSkillBlockCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "AI技能", groupName = "AI技能", allowDynamicAttrKey = false)
public class AiSkillServiceImpl extends SkyeyeBusinessServiceImpl<AiSkillDao, AiSkill> implements AiSkillService {

    @Autowired
    @Lazy
    private AiSkillSuiteService aiSkillSuiteService;

    @Override
    public void validatorEntity(AiSkill entity) {
        super.validatorEntity(entity);
        if (StrUtil.isBlank(entity.getAppId()) || StrUtil.isBlank(entity.getServiceClassName())) {
            throw new CustomException("请绑定业务对象 appId 与 serviceClassName");
        }
        if (StrUtil.isBlank(entity.getOddNumber())) {
            entity.setOddNumber("skill" + ToolUtil.getSurFaceId().substring(0, 8));
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(EnableEnum.ENABLE_USING.getKey());
        }
        if (entity.getOrderBy() == null) {
            entity.setOrderBy(100);
        }
        String compiled = AiSkillBlockCompiler.compile(entity.getBlocks());
        if (StrUtil.isNotBlank(compiled)) {
            entity.setInstruction(compiled);
        }
        if (StrUtil.isNotBlank(entity.getSuiteId())) {
            AiSkillSuite suite = aiSkillSuiteService.selectById(entity.getSuiteId());
            if (suite == null || StrUtil.isBlank(suite.getId())) {
                throw new CustomException("所属套件不存在");
            }
        }
        disableOtherEnabled(entity);
    }

    /**
     * 同一业务对象只允许一条技能处于启用。启用当前技能时，自动禁用同对象下其它启用技能。
     */
    private void disableOtherEnabled(AiSkill entity) {
        if (!EnableEnum.ENABLE_USING.getKey().equals(entity.getEnabled())) {
            return;
        }
        UpdateWrapper<AiSkill> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getAppId), entity.getAppId());
        updateWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getServiceClassName), entity.getServiceClassName());
        updateWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getEnabled), EnableEnum.ENABLE_USING.getKey());
        if (StrUtil.isNotBlank(entity.getId())) {
            updateWrapper.ne(CommonConstants.ID, entity.getId());
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(AiSkill::getEnabled), EnableEnum.DISABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public AiSkill selectById(String id) {
        AiSkill skill = super.selectById(id);
        if (skill != null && StrUtil.isNotBlank(skill.getSuiteId())) {
            aiSkillSuiteService.setDataMation(skill, AiSkill::getSuiteId);
        }
        return skill;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        aiSkillSuiteService.setMationForMap(beans, "suiteId", "suiteMation");
        return beans;
    }

    @Override
    protected QueryWrapper<AiSkill> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AiSkill> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotBlank(commonPageInfo.getServiceAppId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getAppId), commonPageInfo.getServiceAppId());
        }
        if (StrUtil.isNotBlank(commonPageInfo.getServiceClassName())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getServiceClassName), commonPageInfo.getServiceClassName());
        }
        if (StrUtil.isNotBlank(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getSuiteId), commonPageInfo.getObjectId());
        }
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AiSkill::getOrderBy));
        return queryWrapper;
    }

    @Override
    public List<AiSkill> queryByBiz(String appId, String serviceClassName) {
        QueryWrapper<AiSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getServiceClassName), serviceClassName);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AiSkill::getOrderBy));
        List<AiSkill> list = list(queryWrapper);
        fillSuite(list);
        return list;
    }

    @Override
    public List<AiSkill> queryEnabledList() {
        QueryWrapper<AiSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AiSkill::getOrderBy));
        List<AiSkill> list = list(queryWrapper);
        fillSuite(list);
        return list;
    }

    @Override
    public List<AiSkill> queryBySuiteId(String suiteId) {
        if (StrUtil.isBlank(suiteId)) {
            return java.util.Collections.emptyList();
        }
        QueryWrapper<AiSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getSuiteId), suiteId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AiSkill::getOrderBy));
        return list(queryWrapper);
    }

    @Override
    public void queryMatchList(InputObject inputObject, OutputObject outputObject) {
        List<AiSkill> skills = queryEnabledList();
        List<AiSkillSuite> suites = aiSkillSuiteService.queryEnabledList();
        Map<String, Object> bean = new HashMap<>();
        bean.put("skillList", skills);
        bean.put("suiteList", suites);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
        if (CollectionUtil.isNotEmpty(skills)) {
            outputObject.setBeans(skills.stream().map(item -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", item.getId());
                row.put("name", item.getName());
                row.put("suiteId", item.getSuiteId());
                return row;
            }).collect(Collectors.toList()));
        }
    }

    private void fillSuite(List<AiSkill> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        aiSkillSuiteService.setDataMation(list, AiSkill::getSuiteId);
    }
}
