/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.key.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.key.dao.AiApiKeyDao;
import com.skyeye.key.entity.AiApiKey;
import com.skyeye.key.service.AiApiKeyService;
import com.skyeye.role.entity.Role;
import com.skyeye.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopDeliveryCompanyController
 * @Description: ai配置服务类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/8 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "AI配置", groupName = "AI配置", tenant = TenantEnum.WEAK_ISOLATION)
public class AiApiKeyServiceImpl extends SkyeyeBusinessServiceImpl<AiApiKeyDao, AiApiKey> implements AiApiKeyService {

    @Autowired
    private RoleService roleService;

    @Override
    public void validatorEntity(AiApiKey aiApiKey) {
        super.validatorEntity(aiApiKey);
        //判断RoleId是否存在
        if (StrUtil.isNotEmpty(aiApiKey.getRoleId())) {
            Role role = roleService.selectById(aiApiKey.getRoleId());
            //判断RoleId是否为空，如果为空，则抛出异常
            if (role.getId() == null) {
                throw new CustomException("角色不存在: " + aiApiKey.getRoleId());
            }
        }
    }

    @Override
    public void writePostpose(AiApiKey entity, String userId) {
        super.writePostpose(entity, userId);
        disableOtherEnabledKeysOfRole(entity);
    }

    @Override
    public AiApiKey selectById(String id) {
        AiApiKey aiApiKey = super.selectById(id);
        roleService.setDataMation(aiApiKey, AiApiKey::getRoleId);
        return aiApiKey;
    }

    @Override
    public QueryWrapper<AiApiKey> getQueryWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<AiApiKey> queryWrapper = super.getQueryWrapper(tableSelectInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiApiKey::getEnabled), EnableEnum.ENABLE_USING.getKey());
        if (StrUtil.isNotEmpty(tableSelectInfo.getCustomParamsMapStr("roleId"))) {
            // 根据角色id查询
            queryWrapper.eq(MybatisPlusUtil.toColumns(AiApiKey::getRoleId), tableSelectInfo.getCustomParamsMapStr("roleId"));
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryDataList(inputObject);
        roleService.setMationForMap(beans, "roleId", "roleMation");
        return beans;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        roleService.setMationForMap(beans, "roleId", "roleMation");
        return beans;
    }

    @Override
    public AiApiKey selectEnabledKey(String apiKeyId) {
        if (StrUtil.isNotEmpty(apiKeyId)) {
            AiApiKey aiApiKey = selectById(apiKeyId);
            if (aiApiKey == null || StrUtil.isEmpty(aiApiKey.getId())) {
                throw new CustomException("AI配置不存在");
            }
            if (!String.valueOf(EnableEnum.ENABLE_USING.getKey()).equals(String.valueOf(aiApiKey.getEnabled()))) {
                throw new CustomException("AI配置已禁用: " + aiApiKey.getName());
            }
            return aiApiKey;
        }
        QueryWrapper<AiApiKey> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiApiKey::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AiApiKey::getCreateTime));
        List<AiApiKey> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            throw new CustomException("未配置可用的AI Key，请先在AI配置中启用一条。");
        }
        return selectById(list.get(0).getId());
    }

    @Override
    @IgnoreTenant
    public AiApiKey selectEnabledKeyByRoleId(String roleId) {
        if (StrUtil.isBlank(roleId)) {
            throw new CustomException("AI角色不能为空");
        }
        QueryWrapper<AiApiKey> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiApiKey::getRoleId), roleId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiApiKey::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AiApiKey::getCreateTime));
        List<AiApiKey> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            throw new CustomException("该AI角色未绑定已启用的AI配置");
        }
        AiApiKey aiApiKey = list.get(0);
        if (list.size() > 1) {
            disableOtherEnabledKeysOfRole(aiApiKey);
        }
        roleService.setDataMation(aiApiKey, AiApiKey::getRoleId);
        return aiApiKey;
    }

    @Override
    @IgnoreTenant
    public AiApiKey selectEnabledKeyByKnowledgeId(String knowledgeId) {
        if (StrUtil.isBlank(knowledgeId)) {
            throw new CustomException("知识库不能为空");
        }
        // join 查询必须 @IgnoreTenant，再手动带表别名过滤租户，否则拦截器会追加无别名的 tenant_id 导致歧义
        MPJLambdaWrapper<AiApiKey> wrapper = JoinWrappers.lambda("ak", AiApiKey.class)
            .selectAll(AiApiKey.class)
            .innerJoin(Role.class, "r", Role::getId, AiApiKey::getRoleId)
            .eq(Role::getKnowledgeId, knowledgeId)
            .eq(AiApiKey::getEnabled, EnableEnum.ENABLE_USING.getKey())
            .orderByDesc(AiApiKey::getCreateTime);
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            wrapper.eq("ak." + CommonConstants.TENANT_ID_FIELD, tenantId);
            wrapper.eq("r." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        List<AiApiKey> list = skyeyeBaseMapper.selectJoinList(AiApiKey.class, wrapper);
        if (CollectionUtil.isEmpty(list)) {
            throw new CustomException("请先在 AI 角色中绑定该知识库，并启用对应 AI 配置");
        }
        return list.get(0);
    }

    private void disableOtherEnabledKeysOfRole(AiApiKey aiApiKey) {
        if (!isEnabled(aiApiKey.getEnabled()) || StrUtil.isBlank(aiApiKey.getRoleId())) {
            return;
        }
        QueryWrapper<AiApiKey> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiApiKey::getRoleId), aiApiKey.getRoleId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiApiKey::getEnabled), EnableEnum.ENABLE_USING.getKey());
        if (StrUtil.isNotBlank(aiApiKey.getId())) {
            queryWrapper.ne(CommonConstants.ID, aiApiKey.getId());
        }
        List<AiApiKey> others = list(queryWrapper);
        if (CollectionUtil.isEmpty(others)) {
            return;
        }
        UpdateWrapper<AiApiKey> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(AiApiKey::getRoleId), aiApiKey.getRoleId());
        updateWrapper.eq(MybatisPlusUtil.toColumns(AiApiKey::getEnabled), EnableEnum.ENABLE_USING.getKey());
        if (StrUtil.isNotBlank(aiApiKey.getId())) {
            updateWrapper.ne(CommonConstants.ID, aiApiKey.getId());
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(AiApiKey::getEnabled), EnableEnum.DISABLE_USING.getKey());
        update(updateWrapper);
        for (AiApiKey other : others) {
            refreshCache(other.getId());
        }
    }

    private boolean isEnabled(String enabled) {
        return String.valueOf(EnableEnum.ENABLE_USING.getKey()).equals(String.valueOf(enabled));
    }
}
