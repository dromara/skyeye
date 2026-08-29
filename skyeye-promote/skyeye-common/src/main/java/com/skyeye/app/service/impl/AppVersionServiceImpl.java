/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.app.dao.AppVersionDao;
import com.skyeye.app.entity.AppRelease;
import com.skyeye.app.entity.AppVersion;
import com.skyeye.app.enums.AppReleaseStatusEnum;
import com.skyeye.app.service.AppReleaseService;
import com.skyeye.app.service.AppStoreService;
import com.skyeye.app.service.AppVersionService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: AppVersionServiceImpl
 * @Description: APP版本管理服务实现层
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "APP版本管理", groupName = "APP版本发布模块", tenant = TenantEnum.PLATE)
public class AppVersionServiceImpl extends SkyeyeBusinessServiceImpl<AppVersionDao, AppVersion> implements AppVersionService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    private AppReleaseService appReleaseService;

    @Autowired
    private AppStoreService appStoreService;

    @Override
    protected QueryWrapper<AppVersion> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AppVersion> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppVersion::getProjectId), commonPageInfo.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppVersion::getPlatform), commonPageInfo.getType());
        return queryWrapper;
    }

    @Override
    protected void validatorEntity(AppVersion entity) {
        super.validatorEntity(entity);
        QueryWrapper<AppVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppVersion::getProjectId), entity.getProjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppVersion::getPlatform), entity.getPlatform());
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(AppVersion::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(AppVersion::getVersionCode), entity.getVersionCode()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        AppVersion appVersion = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(appVersion)) {
            throw new CustomException("该版本名称或版本号已存在，请重新输入！");
        }
        String appFilePath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + entity.getFilePath();
        File appFile = new File(appFilePath);
        entity.setFileSize(String.valueOf(appFile.length()));
    }

    @Override
    protected void writePostpose(AppVersion entity, String userId) {
        super.writePostpose(entity, userId);
        // 处理应用商店发布记录
        handleAppReleaseRecords(entity, userId);
    }

    @Override
    public AppVersion selectById(String id) {
        AppVersion appVersion = super.selectById(id);
        // 获取现有的发布记录
        List<AppRelease> existingReleases = appReleaseService.selectByVersionIdAndProjectId(
            appVersion.getId(), appVersion.getProjectId(), StrUtil.EMPTY, StrUtil.EMPTY);
        List<String> storeIdList = existingReleases.stream().map(AppRelease::getStoreId).collect(Collectors.toList());
        appVersion.setStoreIdList(storeIdList);
        return appVersion;
    }

    @Override
    @IgnoreTenant
    public AppVersion getLatestVersionByProjectAndPlatform(String projectId, String platform, String storeKey) {
        QueryWrapper<AppVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppVersion::getProjectId), projectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppVersion::getPlatform), platform);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AppVersion::getLastUpdateTime));
        queryWrapper.last("LIMIT 1");

        AppVersion latestVersion = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(latestVersion)) {
            return null;
        }
        // 获取现有的发布记录
        List<AppRelease> existingReleases = appReleaseService.selectByVersionIdAndProjectId(
            latestVersion.getId(), latestVersion.getProjectId(), storeKey, AppReleaseStatusEnum.PUBLISHED.getKey());
        if (CollectionUtil.isEmpty(existingReleases)) {
            return null;
        }
        return latestVersion;
    }

    /**
     * 处理应用商店发布记录
     * 根据 AppVersion 的 storeIdList 和现有的 AppRelease 记录进行对比
     * 删除不再需要的记录，新增新的记录
     *
     * @param entity APP版本实体
     * @param userId 用户ID
     */
    private void handleAppReleaseRecords(AppVersion entity, String userId) {
        log.info("开始处理APP版本[{}]的应用商店发布记录，用户ID：{}", entity.getId(), userId);

        try {
            // 获取新的应用商店ID列表
            List<String> newStoreIdList = entity.getStoreIdList();

            // 获取现有的发布记录
            List<AppRelease> existingReleases = appReleaseService.selectByVersionIdAndProjectId(
                entity.getId(), entity.getProjectId(), StrUtil.EMPTY, StrUtil.EMPTY);

            // 获取现有记录中的应用商店ID列表
            List<String> existingStoreIds = existingReleases.stream()
                .map(AppRelease::getStoreId)
                .collect(Collectors.toList());

            // 找出需要删除的记录（现有记录中的应用商店ID不在新的列表中）
            List<String> storeIdsToDelete = existingStoreIds.stream()
                .filter(storeId -> !newStoreIdList.contains(storeId))
                .collect(Collectors.toList());

            // 找出需要新增的应用商店ID（新的应用商店ID不在现有记录中）
            List<String> storeIdsToAdd = newStoreIdList.stream()
                .filter(storeId -> !existingStoreIds.contains(storeId))
                .collect(Collectors.toList());

            log.info("APP版本[{}]发布记录处理：现有商店[{}]，新选择商店[{}]，需删除[{}]，需新增[{}]",
                entity.getId(), existingStoreIds, newStoreIdList, storeIdsToDelete, storeIdsToAdd);

            // 删除不再需要的发布记录
            if (CollectionUtil.isNotEmpty(storeIdsToDelete)) {
                deleteReleaseRecordsByStoreIds(existingReleases, storeIdsToDelete);
            }

            // 新增新的发布记录
            if (CollectionUtil.isNotEmpty(storeIdsToAdd)) {
                createNewReleaseRecords(entity, storeIdsToAdd);
            }

            log.info("APP版本[{}]应用商店发布记录处理完成", entity.getId());
        } catch (Exception e) {
            log.error("处理APP版本[{}]应用商店发布记录时发生异常", entity.getId(), e);
            throw new CustomException("处理应用商店发布记录失败：" + e.getMessage());
        }
    }

    /**
     * 根据应用商店ID列表删除发布记录
     * 只删除那些可以安全删除的记录（如待发布、已取消状态）
     *
     * @param existingReleases 现有的发布记录
     * @param storeIdsToDelete 要删除的应用商店ID列表
     */
    private void deleteReleaseRecordsByStoreIds(List<AppRelease> existingReleases, List<String> storeIdsToDelete) {
        List<String> idsToDelete = existingReleases.stream()
            .filter(release -> storeIdsToDelete.contains(release.getStoreId()))
            .filter(release -> AppReleaseStatusEnum.isEditableStatus(release.getStatus()))
            .map(AppRelease::getId)
            .collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(idsToDelete)) {
            appReleaseService.deleteById(idsToDelete);
        }
    }

    /**
     * 创建新的发布记录
     *
     * @param entity        APP版本实体
     * @param storeIdsToAdd 要新增的应用商店ID列表
     */
    private void createNewReleaseRecords(AppVersion entity, List<String> storeIdsToAdd) {
        List<AppRelease> newReleases = new ArrayList<>();

        for (String storeId : storeIdsToAdd) {
            AppRelease appRelease = new AppRelease();
            appRelease.setPlatform(entity.getPlatform());
            appRelease.setStoreId(storeId);
            appRelease.setStatus(AppReleaseStatusEnum.PENDING.getKey()); // 默认状态为待发布
            newReleases.add(appRelease);
        }

        if (CollectionUtil.isNotEmpty(newReleases)) {
            appReleaseService.saveList(entity.getId(), entity.getProjectId(), newReleases);
        }
    }
}
