/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentarchive.entity.EquipmentArchive;
import com.skyeye.equipmentarchive.service.EquipmentArchiveService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionStatPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionStatQuerySupport
 * @Description: 设备巡检统计查询条件处理
 */
@Component
public class EquipmentInspectionStatQuerySupport {

    @Autowired
    private EquipmentArchiveService equipmentArchiveService;

    public EquipmentInspectionStatPageInfo buildPageInfo(InputObject inputObject) {
        Map<String, Object> params = inputObject.getParams();
        if (!params.containsKey("page") || StrUtil.isBlank(MapUtil.getStr(params, "page"))) {
            params.put("page", 1);
        }
        if (!params.containsKey("limit") || StrUtil.isBlank(MapUtil.getStr(params, "limit"))) {
            params.put("limit", 20);
        }
        EquipmentInspectionStatPageInfo pageInfo = inputObject.getParams(EquipmentInspectionStatPageInfo.class);
        fillDefaultTimeRange(pageInfo);
        return pageInfo;
    }

    public EquipmentInspectionStatPageInfo buildQueryInfo(InputObject inputObject) {
        TableSelectInfo query = inputObject.getParams(TableSelectInfo.class);
        EquipmentInspectionStatPageInfo pageInfo = BeanUtil.toBean(query, EquipmentInspectionStatPageInfo.class);
        fillDefaultTimeRange(pageInfo);
        return pageInfo;
    }

    public void fillDefaultTimeRange(EquipmentInspectionStatPageInfo pageInfo) {
        Date now = new Date();
        if (StrUtil.isBlank(pageInfo.getStartTime())) {
            pageInfo.setStartTime(DateUtil.format(DateUtil.beginOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        }
        if (StrUtil.isBlank(pageInfo.getEndTime())) {
            pageInfo.setEndTime(DateUtil.format(DateUtil.endOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        }
    }

    public boolean prepareEquipmentScope(EquipmentInspectionStatPageInfo pageInfo) {
        Set<String> scopedIds = resolveEquipmentIdScope(pageInfo);
        if (scopedIds == null) {
            pageInfo.setEquipmentIdList(null);
            return false;
        }
        if (scopedIds.isEmpty()) {
            return true;
        }
        pageInfo.setEquipmentIdList(new ArrayList<>(scopedIds));
        return false;
    }

    public Set<String> resolveEquipmentIdScope(EquipmentInspectionStatPageInfo pageInfo) {
        List<String> objectIdList = splitObjectIds(pageInfo.getObjectId());
        boolean hasObjectFilter = CollectionUtil.isNotEmpty(objectIdList);
        boolean hasKeyword = StrUtil.isNotBlank(pageInfo.getKeyword());
        boolean hasHolder = StrUtil.isNotBlank(pageInfo.getHolderId());
        if (!hasObjectFilter && !hasKeyword && !hasHolder) {
            return null;
        }
        Set<String> ids = new LinkedHashSet<>();
        if (hasObjectFilter) {
            ids.addAll(objectIdList);
        }
        if (hasKeyword || hasHolder) {
            QueryWrapper<EquipmentArchive> archiveWrapper = new QueryWrapper<>();
            if (hasKeyword) {
                archiveWrapper.and(w -> w.like(MybatisPlusUtil.toColumns(EquipmentArchive::getName), pageInfo.getKeyword())
                    .or().like(MybatisPlusUtil.toColumns(EquipmentArchive::getOddNumber), pageInfo.getKeyword()));
            }
            if (hasHolder) {
                archiveWrapper.eq(MybatisPlusUtil.toColumns(EquipmentArchive::getUseFarm), pageInfo.getHolderId());
            }
            equipmentArchiveService.list(archiveWrapper).forEach(item -> ids.add(item.getId()));
        }
        if (hasObjectFilter) {
            ids.retainAll(new LinkedHashSet<>(objectIdList));
        }
        return ids;
    }

    public List<String> splitObjectIds(String objectId) {
        if (StrUtil.isBlank(objectId)) {
            return Collections.emptyList();
        }
        return Arrays.stream(objectId.split(","))
            .map(String::trim)
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toList());
    }

    public boolean isStatPageQuery(InputObject inputObject) {
        return inputObject.getParams(CommonPageInfo.class) instanceof EquipmentInspectionStatPageInfo;
    }

    public void applyOrderQueryWrapper(QueryWrapper<EquipmentInspectionOrder> queryWrapper, CommonPageInfo commonPageInfo) {
        if (commonPageInfo instanceof EquipmentInspectionStatPageInfo) {
            EquipmentInspectionStatPageInfo pageInfo = (EquipmentInspectionStatPageInfo) commonPageInfo;
            fillDefaultTimeRange(pageInfo);
            if (prepareEquipmentScope(pageInfo)) {
                queryWrapper.apply("1 = 0");
            } else {
                if (StrUtil.isNotEmpty(pageInfo.getStartTime())) {
                    queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), pageInfo.getStartTime());
                }
                if (StrUtil.isNotEmpty(pageInfo.getEndTime())) {
                    queryWrapper.le(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), pageInfo.getEndTime());
                }
                if (pageInfo.getOverallResult() != null) {
                    queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getOverallResult), pageInfo.getOverallResult());
                }
                if (CollectionUtil.isNotEmpty(pageInfo.getEquipmentIdList())) {
                    queryWrapper.in(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), pageInfo.getEquipmentIdList());
                }
            }
        } else {
            if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), commonPageInfo.getObjectId());
            }
            if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
                applyHolderEquipmentFilter(queryWrapper, commonPageInfo.getHolderId());
            }
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCreateTime));
    }

    private void applyHolderEquipmentFilter(QueryWrapper<EquipmentInspectionOrder> queryWrapper, String holderId) {
        List<String> equipmentIdList = listArchiveIdsByUseFarm(holderId);
        if (CollectionUtil.isEmpty(equipmentIdList)) {
            queryWrapper.apply("1 = 0");
        } else {
            queryWrapper.in(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), equipmentIdList);
        }
    }

    public List<String> listArchiveIdsByUseFarm(String useFarm) {
        QueryWrapper<EquipmentArchive> archiveWrapper = new QueryWrapper<>();
        archiveWrapper.eq(MybatisPlusUtil.toColumns(EquipmentArchive::getUseFarm), useFarm);
        archiveWrapper.select(MybatisPlusUtil.toColumns(EquipmentArchive::getId));
        return equipmentArchiveService.list(archiveWrapper).stream()
            .map(EquipmentArchive::getId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    public void appendStatRecordDisplayFields(List<Map<String, Object>> beans) {
        for (Map<String, Object> bean : beans) {
            String photoUrls = MapUtil.getStr(bean, "headerPhotoUrls");
            bean.put("photo", photoUrls);
            bean.put("photoUrls", photoUrls);
            String location = StrUtil.blankToDefault(MapUtil.getStr(bean, "locationText"),
                MapUtil.getStr(bean, "headerLocationText"));
            bean.put("location", location);
            if (StrUtil.isBlank(MapUtil.getStr(bean, "farmName"))) {
                Map<String, Object> equipmentMation = BeanUtil.toBean(bean.get("equipmentMation"), Map.class);
                if (equipmentMation != null) {
                    bean.put("farmName", equipmentMation.get("useFarm"));
                }
            }
        }
    }
}
