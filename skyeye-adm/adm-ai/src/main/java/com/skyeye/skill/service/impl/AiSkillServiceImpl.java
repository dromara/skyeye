/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.pro.rest.IPlatformBaseSettingRest;
import com.skyeye.skill.classenum.AiSkillUseSceneEnum;
import com.skyeye.skill.dao.AiSkillDao;
import com.skyeye.skill.entity.AiSkill;
import com.skyeye.skill.entity.AiSkillCategory;
import com.skyeye.skill.entity.AiSkillSuite;
import com.skyeye.skill.service.AiSkillCategoryService;
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

    @Autowired
    @Lazy
    private AiSkillCategoryService aiSkillCategoryService;

    @Autowired
    private IPlatformBaseSettingRest iPlatformBaseSettingRest;

    @Override
    public void validatorEntity(AiSkill entity) {
        super.validatorEntity(entity);
        entity.setUseScene(AiSkillUseSceneEnum.normalize(entity.getUseScene()));
        // 编码一律后端生成/保留：新增取号，编辑沿用库中原值，忽略前端传入
        if (StrUtil.isBlank(entity.getId())) {
            entity.setOddNumber(generateSkillCode(entity));
        } else {
            AiSkill dbSkill = getById(entity.getId());
            if (dbSkill != null && StrUtil.isNotBlank(dbSkill.getOddNumber())) {
                entity.setOddNumber(dbSkill.getOddNumber());
            } else if (StrUtil.isBlank(entity.getOddNumber())) {
                entity.setOddNumber(generateSkillCode(entity));
            }
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
        if (StrUtil.isNotBlank(entity.getCategoryId())) {
            AiSkillCategory category = aiSkillCategoryService.selectById(entity.getCategoryId());
            if (category == null || StrUtil.isBlank(category.getId())) {
                throw new CustomException("所属分类不存在");
            }
        }
        disableOtherEnabled(entity);
    }

    @Override
    protected void createPrepose(AiSkill entity) {
        super.createPrepose(entity);
        if (StrUtil.isBlank(entity.getAppId()) || StrUtil.isBlank(entity.getServiceClassName())) {
            throw new CustomException("请绑定业务对象 appId 与 serviceClassName");
        }
    }

    /**
     * 优先按平台「AI技能编码」规则取号；未配置时回退随机编码。
     */
    private String generateSkillCode(AiSkill entity) {
        try {
            Map<String, Object> bean = ExecuteFeignClient.get(
                () -> iPlatformBaseSettingRest.queryPlatformAiSkillCodeRule()).getBean();
            Object ruleId = bean == null ? null : bean.get("aiSkillCodeRuleId");
            if (ruleId != null && StrUtil.isNotBlank(ruleId.toString())) {
                Map<String, Object> business = BeanUtil.beanToMap(entity);
                String code = iCodeRuleService.getNextCode(ruleId.toString(), business);
                if (StrUtil.isNotBlank(code)) {
                    return code;
                }
            }
        } catch (Exception e) {
            // 规则未配或取号失败时走随机兜底，避免阻断保存
        }
        return "skill" + ToolUtil.getSurFaceId().substring(0, 8);
    }

    /**
     * 按使用位置互斥：同一业务对象下，聊天/表单各自最多一条启用技能；
     * 「仅编码调用」不参与互斥，可与其它位置并存、也可多条启用。
     */
    private void disableOtherEnabled(AiSkill entity) {
        if (!EnableEnum.ENABLE_USING.getKey().equals(entity.getEnabled())) {
            return;
        }
        if (AiSkillUseSceneEnum.isCodeOnly(entity.getUseScene())) {
            return;
        }
        QueryWrapper<AiSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getAppId), entity.getAppId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getServiceClassName), entity.getServiceClassName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getEnabled), EnableEnum.ENABLE_USING.getKey());
        if (StrUtil.isNotBlank(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        List<AiSkill> others = list(queryWrapper);
        if (CollectionUtil.isEmpty(others)) {
            return;
        }
        List<String> disableIds = others.stream()
            .filter(item -> !AiSkillUseSceneEnum.isCodeOnly(item.getUseScene()))
            .filter(item -> AiSkillUseSceneEnum.overlapsUiScene(entity.getUseScene(), item.getUseScene()))
            .map(AiSkill::getId)
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(disableIds)) {
            return;
        }
        UpdateWrapper<AiSkill> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(CommonConstants.ID, disableIds);
        updateWrapper.set(MybatisPlusUtil.toColumns(AiSkill::getEnabled), EnableEnum.DISABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public AiSkill selectById(String id) {
        AiSkill skill = super.selectById(id);
        if (skill != null) {
            if (StrUtil.isNotBlank(skill.getSuiteId())) {
                aiSkillSuiteService.setDataMation(skill, AiSkill::getSuiteId);
            }
            if (StrUtil.isNotBlank(skill.getCategoryId())) {
                aiSkillCategoryService.setDataMation(skill, AiSkill::getCategoryId);
            }
        }
        return skill;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        aiSkillSuiteService.setMationForMap(beans, "suiteId", "suiteMation");
        aiSkillCategoryService.setMationForMap(beans, "categoryId", "categoryMation");
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
        fillRefs(list);
        return list;
    }

    @Override
    public List<AiSkill> queryEnabledList() {
        QueryWrapper<AiSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(AiSkill::getOrderBy));
        List<AiSkill> list = list(queryWrapper);
        fillRefs(list);
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
    public List<AiSkill> queryByCategoryId(String categoryId) {
        if (StrUtil.isBlank(categoryId)) {
            return java.util.Collections.emptyList();
        }
        QueryWrapper<AiSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getCategoryId), categoryId);
        return list(queryWrapper);
    }

    @Override
    public void queryMatchList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String useScene = params == null ? null : String.valueOf(params.getOrDefault("useScene", ""));
        List<AiSkill> skills = queryEnabledList();
        if (StrUtil.isNotBlank(useScene) && !"null".equals(useScene)) {
            AiSkillUseSceneEnum scene = parseScene(useScene);
            if (scene != null) {
                skills = skills.stream()
                    .filter(item -> AiSkillUseSceneEnum.contains(item.getUseScene(), scene))
                    .collect(Collectors.toList());
            }
        }
        List<AiSkillSuite> suites = aiSkillSuiteService.queryEnabledList();
        List<AiSkillCategory> categories = aiSkillCategoryService.queryEnabledList();
        Map<String, Object> bean = new HashMap<>();
        bean.put("skillList", skills);
        bean.put("suiteList", suites);
        bean.put("categoryList", categories);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
        if (CollectionUtil.isNotEmpty(skills)) {
            outputObject.setBeans(skills.stream().map(item -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", item.getId());
                row.put("name", item.getName());
                row.put("suiteId", item.getSuiteId());
                row.put("useScene", item.getUseScene());
                row.put("oddNumber", item.getOddNumber());
                return row;
            }).collect(Collectors.toList()));
        }
    }

    @Override
    public AiSkill queryByOddNumber(String oddNumber) {
        if (StrUtil.isBlank(oddNumber)) {
            return null;
        }
        QueryWrapper<AiSkill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiSkill::getOddNumber), oddNumber.trim());
        AiSkill skill = getOne(queryWrapper, false);
        if (skill != null) {
            return selectById(skill.getId());
        }
        return null;
    }

    @Override
    public void queryAiSkillByOddNumber(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String oddNumber = params.get("oddNumber") == null ? StrUtil.EMPTY : params.get("oddNumber").toString();
        if (StrUtil.isBlank(oddNumber)) {
            throw new CustomException("技能编码不能为空");
        }
        AiSkill skill = queryByOddNumber(oddNumber);
        if (skill == null) {
            throw new CustomException("技能编码不存在：" + oddNumber);
        }
        if (!EnableEnum.ENABLE_USING.getKey().equals(skill.getEnabled())) {
            throw new CustomException("技能未启用：" + oddNumber);
        }
        outputObject.setBean(skill);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private AiSkillUseSceneEnum parseScene(String useScene) {
        try {
            return AiSkillUseSceneEnum.getByKey(Integer.valueOf(useScene.trim()));
        } catch (Exception e) {
            return null;
        }
    }

    private void fillRefs(List<AiSkill> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        aiSkillSuiteService.setDataMation(list, AiSkill::getSuiteId);
        aiSkillCategoryService.setDataMation(list, AiSkill::getCategoryId);
    }
}
