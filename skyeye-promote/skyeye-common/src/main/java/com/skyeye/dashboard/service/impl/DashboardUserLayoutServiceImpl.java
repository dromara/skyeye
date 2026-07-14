/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dashboard.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dashboard.dao.DashboardUserLayoutDao;
import com.skyeye.dashboard.entity.DashboardUserLayout;
import com.skyeye.dashboard.service.DashboardUserLayoutService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DashboardUserLayoutServiceImpl
 * @Description: 用户仪表盘布局服务层
 */
@Service
@SkyeyeService(name = "用户仪表盘布局", groupName = "仪表盘设计器", tenant = TenantEnum.STRONG_ISOLATION)
public class DashboardUserLayoutServiceImpl extends SkyeyeBusinessServiceImpl<DashboardUserLayoutDao, DashboardUserLayout>
    implements DashboardUserLayoutService {

    @Override
    public void queryDashboardLayoutList(InputObject inputObject, OutputObject outputObject) {
        String userId = getBaseUserId(inputObject);
        QueryWrapper<DashboardUserLayout> queryWrapper = buildUserQueryWrapper(userId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DashboardUserLayout::getIsDefault));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DashboardUserLayout::getCreateTime));
        List<DashboardUserLayout> layoutList = list(queryWrapper);
        outputObject.setBeans(layoutList);
        outputObject.settotal(layoutList.size());
    }

    @Override
    public void queryDefaultDashboardLayout(InputObject inputObject, OutputObject outputObject) {
        String userId = getBaseUserId(inputObject);
        DashboardUserLayout layout = getDefaultLayout(userId);
        outputObject.setBean(layout);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void setDefaultDashboardLayoutById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String userId = getBaseUserId(inputObject);
        DashboardUserLayout layout = selectById(id);
        validateOwner(layout, userId);
        clearDefaultFlag(userId);
        layout.setIsDefault(IsDefaultEnum.IS_DEFAULT.getKey());
        updateEntity(layout, userId);
        outputObject.setBean(selectById(id));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteDashboardLayoutById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String userId = getBaseUserId(inputObject);
        DashboardUserLayout layout = selectById(id);
        validateOwner(layout, userId);
        deleteById(id);
    }

    @Override
    protected void createPrepose(DashboardUserLayout entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        entity.setUserId(userId);
        if (StrUtil.isBlank(entity.getName())) {
            entity.setName("我的仪表盘");
        }
        if (entity.getIsDefault() == null) {
            entity.setIsDefault(IsDefaultEnum.NOT_DEFAULT.getKey());
        }
        if (countUserLayouts(userId) == 0) {
            entity.setIsDefault(IsDefaultEnum.IS_DEFAULT.getKey());
        }
        if (IsDefaultEnum.IS_DEFAULT.getKey().equals(entity.getIsDefault())) {
            clearDefaultFlag(userId);
        }
    }

    @Override
    protected void updatePrepose(DashboardUserLayout entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        DashboardUserLayout oldLayout = selectById(entity.getId());
        validateOwner(oldLayout, userId);
        entity.setUserId(oldLayout.getUserId());
        if (StrUtil.isBlank(entity.getName())) {
            entity.setName(oldLayout.getName());
        }
        if (entity.getIsDefault() == null) {
            entity.setIsDefault(oldLayout.getIsDefault());
        }
        if (IsDefaultEnum.IS_DEFAULT.getKey().equals(entity.getIsDefault())) {
            clearDefaultFlagExcept(userId, entity.getId());
        }
    }

    @Override
    protected void validatorEntity(DashboardUserLayout entity) {
        super.validatorEntity(entity);
        if (StrUtil.isNotBlank(entity.getId())) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            DashboardUserLayout dbLayout = selectById(entity.getId());
            validateOwner(dbLayout, userId);
        }
    }

    private DashboardUserLayout getDefaultLayout(String userId) {
        QueryWrapper<DashboardUserLayout> queryWrapper = buildUserQueryWrapper(userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DashboardUserLayout::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
        return getOne(queryWrapper);
    }

    private long countUserLayouts(String userId) {
        return count(buildUserQueryWrapper(userId));
    }

    private void clearDefaultFlag(String userId) {
        UpdateWrapper<DashboardUserLayout> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DashboardUserLayout::getUserId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(DashboardUserLayout::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
        update(updateWrapper);
    }

    private void clearDefaultFlagExcept(String userId, String exceptId) {
        UpdateWrapper<DashboardUserLayout> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DashboardUserLayout::getUserId), userId);
        updateWrapper.ne(CommonConstants.ID, exceptId);
        updateWrapper.set(MybatisPlusUtil.toColumns(DashboardUserLayout::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
        update(updateWrapper);
    }

    private QueryWrapper<DashboardUserLayout> buildUserQueryWrapper(String userId) {
        QueryWrapper<DashboardUserLayout> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DashboardUserLayout::getUserId), userId);
        return queryWrapper;
    }

    private void validateOwner(DashboardUserLayout layout, String userId) {
        if (layout == null || !userId.equals(layout.getUserId())) {
            throw new CustomException("布局不存在或无权限访问.");
        }
    }

    private String getBaseUserId(InputObject inputObject) {
        Map<String, Object> user = inputObject.getLogParams();
        return user.get("id").toString();
    }

}
