/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.impexp.dao.ImportExportApplicableObjectsDao;
import com.skyeye.impexp.entity.ImportExportApplicableObjects;
import com.skyeye.impexp.service.ImportExportApplicableObjectsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ImportExportApplicableObjectsServiceImpl
 * @Description: 导入导出模板适用对象服务实现-强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2026/9/10
 */
@Service
@SkyeyeService(name = "导入导出适用对象", groupName = "系统公共模块", manageShow = false, allowDynamicAttrKey = false)
public class ImportExportApplicableObjectsServiceImpl extends SkyeyeBusinessServiceImpl<ImportExportApplicableObjectsDao, ImportExportApplicableObjects> implements ImportExportApplicableObjectsService {

    @Override
    public void deleteApplicableObjectsByConfigId(String configId) {
        if (StrUtil.isBlank(configId)) {
            return;
        }
        QueryWrapper<ImportExportApplicableObjects> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportApplicableObjects::getConfigId), configId);
        remove(queryWrapper);
    }

    @Override
    public void saveApplicableObjects(String configId, List<ImportExportApplicableObjects> applicableObjectsList) {
        deleteApplicableObjectsByConfigId(configId);
        if (CollectionUtil.isEmpty(applicableObjectsList)) {
            return;
        }
        for (ImportExportApplicableObjects item : applicableObjectsList) {
            item.setConfigId(configId);
            if (StrUtil.isBlank(item.getObjectName()) && StrUtil.isNotBlank(item.getName())) {
                item.setObjectName(item.getName());
            }
        }
        createEntity(applicableObjectsList, StrUtil.EMPTY);
    }

    @Override
    public List<ImportExportApplicableObjects> queryApplicableObjectsByConfigId(String configId) {
        QueryWrapper<ImportExportApplicableObjects> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportApplicableObjects::getConfigId), configId);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<ImportExportApplicableObjects>> queryApplicableObjectsByConfigIds(List<String> configIds) {
        if (CollectionUtil.isEmpty(configIds)) {
            return new HashMap<>();
        }
        QueryWrapper<ImportExportApplicableObjects> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ImportExportApplicableObjects::getConfigId), configIds);
        List<ImportExportApplicableObjects> list = list(queryWrapper);
        return list.stream().collect(Collectors.groupingBy(ImportExportApplicableObjects::getConfigId));
    }
}
