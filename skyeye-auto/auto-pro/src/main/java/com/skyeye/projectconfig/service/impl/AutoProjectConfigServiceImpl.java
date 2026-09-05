/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.projectconfig.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.projectconfig.classenum.AutoProjectConfigAuthEnum;
import com.skyeye.projectconfig.dao.AutoProjectConfigDao;
import com.skyeye.projectconfig.entity.AutoProjectConfig;
import com.skyeye.projectconfig.service.AutoProjectConfigService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 项目配置服务实现。
 */
@Service
@SkyeyeService(name = "项目配置", groupName = "项目配置", teamAuth = true)
public class AutoProjectConfigServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoProjectConfigDao, AutoProjectConfig> implements AutoProjectConfigService {

    @Override
    public Class getAuthEnumClass() {
        return AutoProjectConfigAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoProjectConfigAuthEnum.EDIT.getKey());
    }

    /**
     * 按 objectId 补齐主键后再走新增/编辑分支，避免重复保存时因无 id 走创建并触发唯一校验失败。
     */
    @Override
    public String saveOrUpdateEntity(AutoProjectConfig entity, String userId) {
        fillIdByObjectId(entity);
        return super.saveOrUpdateEntity(entity, userId);
    }

    @Override
    public void validatorEntity(AutoProjectConfig entity) {
        fillIdByObjectId(entity);
        super.validatorEntity(entity);
        if (StrUtil.isBlank(entity.getObjectId())) {
            throw new CustomException("所属项目不能为空");
        }
        entity.setEnableEstimateTime(normalizeSwitch(entity.getEnableEstimateTime()));
        entity.setEnableScoreAllocate(normalizeSwitch(entity.getEnableScoreAllocate()));
        AutoProjectConfig exists = getByObjectId(entity.getObjectId());
        if (exists != null && StrUtil.isNotBlank(entity.getId())
            && !StrUtil.equals(entity.getId(), exists.getId())) {
            throw new CustomException("当前项目已存在功能配置");
        }
    }

    @Override
    public void queryAutoProjectConfigByObjectId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        AutoProjectConfig config = getOrDefaultByObjectId(objectId);
        outputObject.setBean(config);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public AutoProjectConfig getOrDefaultByObjectId(String objectId) {
        AutoProjectConfig config = getByObjectId(objectId);
        if (config != null) {
            return config;
        }
        return buildDefault(objectId);
    }

    @Override
    public boolean isEstimateTimeEnabled(String objectId) {
        return isSwitchOn(getOrDefaultByObjectId(objectId).getEnableEstimateTime());
    }

    @Override
    public boolean isScoreAllocateEnabled(String objectId) {
        return isSwitchOn(getOrDefaultByObjectId(objectId).getEnableScoreAllocate());
    }

    private void fillIdByObjectId(AutoProjectConfig entity) {
        if (entity == null || StrUtil.isNotBlank(entity.getId()) || StrUtil.isBlank(entity.getObjectId())) {
            return;
        }
        AutoProjectConfig exists = getByObjectId(entity.getObjectId());
        if (exists != null) {
            entity.setId(exists.getId());
        }
    }

    private AutoProjectConfig getByObjectId(String objectId) {
        if (StrUtil.isBlank(objectId)) {
            return null;
        }
        QueryWrapper<AutoProjectConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoProjectConfig::getObjectId), objectId);
        return getOne(queryWrapper, false);
    }

    private AutoProjectConfig buildDefault(String objectId) {
        AutoProjectConfig config = new AutoProjectConfig();
        config.setObjectId(objectId);
        config.setEnableEstimateTime(IsDefaultEnum.IS_DEFAULT.getKey());
        config.setEnableScoreAllocate(IsDefaultEnum.IS_DEFAULT.getKey());
        return config;
    }

    private Integer normalizeSwitch(Integer value) {
        if (value != null && value.equals(IsDefaultEnum.NOT_DEFAULT.getKey())) {
            return IsDefaultEnum.NOT_DEFAULT.getKey();
        }
        return IsDefaultEnum.IS_DEFAULT.getKey();
    }

    private boolean isSwitchOn(Integer value) {
        return value == null || value.equals(IsDefaultEnum.IS_DEFAULT.getKey());
    }

}
