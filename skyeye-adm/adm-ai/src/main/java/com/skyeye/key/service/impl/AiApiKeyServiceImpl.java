/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.key.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
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
@SkyeyeService(name = "AI配置", groupName = "AI配置")
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
    public AiApiKey selectById(String id) {
        AiApiKey aiApiKey = super.selectById(id);
        roleService.setDataMation(aiApiKey, AiApiKey::getRoleId);
        return aiApiKey;
    }

    @Override
    public QueryWrapper<AiApiKey> getQueryWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<AiApiKey> queryWrapper = super.getQueryWrapper(tableSelectInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AiApiKey::getEnabled), EnableEnum.ENABLE_USING.getKey());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryDataList(inputObject);
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
}
