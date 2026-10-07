/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.menu.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.menu.classenum.MenuPointType;
import com.skyeye.menu.dao.AuthPointDao;
import com.skyeye.menu.entity.AuthPoint;
import com.skyeye.menu.service.AuthPointService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: AuthPointServiceImpl
 * @Description: 菜单权限点管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/7/23 19:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "权限点管理", groupName = "菜单管理", teamAuth = true, tenant = TenantEnum.PLATE, memoryCache = true)
public class AuthPointServiceImpl extends SkyeyeBusinessServiceImpl<AuthPointDao, AuthPoint> implements AuthPointService {

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        TableSelectInfo selectInfo = inputObject.getParams(TableSelectInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryMenuAuthPointList(selectInfo);
        beans.forEach(bean -> {
            bean.put("typeName", MenuPointType.getTypeName(Integer.parseInt(bean.get("type").toString())));
        });
        return beans;
    }

    @Override
    protected void createPrepose(AuthPoint entity) {
        entity.setMenuNum(String.valueOf(DateUtil.getTimeStampAndToString()));
    }

    @Override
    protected void validatorEntity(AuthPoint entity) {
        super.validatorEntity(entity);
        // 名称 + 接口URL 同时相同才算重复（且关系）
        QueryWrapper<AuthPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AuthPoint::getName), entity.getName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AuthPoint::getAuthMenu), entity.getAuthMenu());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AuthPoint::getObjectId), entity.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AuthPoint::getParentId), entity.getParentId());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        AuthPoint checkSysMenuAuthPoint = getOne(queryWrapper);

        if (!ObjectUtils.isEmpty(checkSysMenuAuthPoint)) {
            throw new CustomException("该菜单下已存在相同名称且相同接口URL的权限点，请进行更改。");
        }
    }

    @Override
    @IgnoreTenant
    public List<AuthPoint> selectByIds(String... ids) {
        return super.selectByIds(ids);
    }

    /**
     * 一键迁移：object_id / object_key 字段标注不可改，需用 UpdateWrapper 强制更新。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void migrateAuthPoint(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String sourceObjectId = params.get("sourceObjectId").toString();
        String sourceObjectKey = params.get("sourceObjectKey").toString();
        String targetObjectId = params.get("targetObjectId").toString();
        String namePrefix = params.get("namePrefix").toString().trim();
        if (StrUtil.equals(sourceObjectId, targetObjectId)) {
            throw new CustomException("目标菜单不能与当前菜单相同");
        }

        QueryWrapper<AuthPoint> sourceQw = new QueryWrapper<>();
        sourceQw.eq(MybatisPlusUtil.toColumns(AuthPoint::getObjectId), sourceObjectId);
        sourceQw.eq(MybatisPlusUtil.toColumns(AuthPoint::getObjectKey), sourceObjectKey);
        List<AuthPoint> sourceList = list(sourceQw);
        if (CollectionUtil.isEmpty(sourceList)) {
            throw new CustomException("当前菜单下没有可迁移的权限点");
        }

        QueryWrapper<AuthPoint> targetQw = new QueryWrapper<>();
        targetQw.eq(MybatisPlusUtil.toColumns(AuthPoint::getObjectId), targetObjectId);
        targetQw.eq(MybatisPlusUtil.toColumns(AuthPoint::getObjectKey), sourceObjectKey);
        List<AuthPoint> targetList = list(targetQw);
        // 名称 + 接口URL 同时相同才冲突
        Set<String> targetDupKeys = new HashSet<>();
        for (AuthPoint bean : targetList) {
            targetDupKeys.add(buildDupKey(bean.getParentId(), bean.getName(), bean.getAuthMenu()));
        }

        for (AuthPoint bean : sourceList) {
            String newName = StrUtil.isBlank(namePrefix) ? bean.getName() : namePrefix + bean.getName();
            String dupKey = buildDupKey(bean.getParentId(), newName, bean.getAuthMenu());
            if (targetDupKeys.contains(dupKey)) {
                throw new CustomException("目标菜单下已存在相同名称且相同接口URL的权限点：" + newName + " / " + StrUtil.blankToDefault(bean.getAuthMenu(), "-"));
            }
        }

        List<String> ids = sourceList.stream().map(AuthPoint::getId).collect(Collectors.toList());
        for (AuthPoint bean : sourceList) {
            String newName = StrUtil.isBlank(namePrefix) ? bean.getName() : namePrefix + bean.getName();
            UpdateWrapper<AuthPoint> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, bean.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(AuthPoint::getObjectId), targetObjectId);
            if (StrUtil.isNotBlank(namePrefix)) {
                updateWrapper.set(MybatisPlusUtil.toColumns(AuthPoint::getName), newName);
            }
            update(updateWrapper);
        }
        clearCache(ids);
        outputObject.settotal(ids.size());
    }

    private static String buildDupKey(String parentId, String name, String authMenu) {
        return StrUtil.blankToDefault(parentId, "0")
            + StrUtil.UNDERLINE + StrUtil.nullToEmpty(name)
            + StrUtil.UNDERLINE + StrUtil.nullToEmpty(authMenu);
    }
}
