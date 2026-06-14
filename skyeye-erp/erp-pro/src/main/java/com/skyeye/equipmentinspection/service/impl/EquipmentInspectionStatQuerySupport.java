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
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentarchive.entity.EquipmentArchive;
import com.skyeye.equipmentarchive.service.EquipmentArchiveService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
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

    public CommonPageInfo buildPageInfo(InputObject inputObject) {
        Map<String, Object> params = inputObject.getParams();
        if (!params.containsKey("page") || StrUtil.isBlank(MapUtil.getStr(params, "page"))) {
            params.put("page", 1);
        }
        if (!params.containsKey("limit") || StrUtil.isBlank(MapUtil.getStr(params, "limit"))) {
            params.put("limit", 20);
        }
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        fillDefaultTimeRange(pageInfo);
        return pageInfo;
    }

    public void fillDefaultTimeRange(CommonPageInfo pageInfo) {
        Date now = new Date();
        if (StrUtil.isBlank(pageInfo.getStartTime())) {
            pageInfo.setStartTime(DateUtil.format(DateUtil.beginOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        }
        if (StrUtil.isBlank(pageInfo.getEndTime())) {
            pageInfo.setEndTime(DateUtil.format(DateUtil.endOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        }
    }

    public EquipmentScope resolveEquipmentScope(CommonPageInfo pageInfo) {
        Set<String> scopedIds = resolveEquipmentIdScope(pageInfo);
        if (scopedIds == null) {
            return EquipmentScope.noFilter();
        }
        if (scopedIds.isEmpty()) {
            return EquipmentScope.empty();
        }
        return EquipmentScope.of(new ArrayList<>(scopedIds));
    }

    public Set<String> resolveEquipmentIdScope(CommonPageInfo pageInfo) {
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
        Map<String, Object> params = inputObject.getParams();
        return StrUtil.isNotBlank(MapUtil.getStr(params, "startTime"))
            || StrUtil.isNotBlank(MapUtil.getStr(params, "endTime"));
    }

    public void applyOrderQueryWrapper(QueryWrapper<EquipmentInspectionOrder> queryWrapper, CommonPageInfo commonPageInfo) {
        boolean statTimeQuery = StrUtil.isNotBlank(commonPageInfo.getStartTime())
            || StrUtil.isNotBlank(commonPageInfo.getEndTime());
        if (statTimeQuery) {
            fillDefaultTimeRange(commonPageInfo);
            if (StrUtil.isNotEmpty(commonPageInfo.getStartTime())) {
                queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), commonPageInfo.getStartTime());
            }
            if (StrUtil.isNotEmpty(commonPageInfo.getEndTime())) {
                queryWrapper.le(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), commonPageInfo.getEndTime());
            }
            Integer overallResult = resolveOverallResult();
            if (overallResult != null) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getOverallResult), overallResult);
            }
        }
        EquipmentScope scope = resolveEquipmentScope(commonPageInfo);
        if (scope.isEmptyResult()) {
            queryWrapper.apply("1 = 0");
        } else if (scope.hasFilter()) {
            queryWrapper.in(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), scope.getEquipmentIdList());
        } else if (!statTimeQuery) {
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

    private Integer resolveOverallResult() {
        Map<String, Object> params = InputObject.getLogParamsStatic();
        if (MapUtil.isEmpty(params)) {
            return null;
        }
        String text = MapUtil.getStr(params, "overallResult");
        if (StrUtil.isBlank(text) || !StrUtil.isNumeric(text)) {
            return null;
        }
        return Integer.parseInt(text);
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

    static final class EquipmentScope {

        private final boolean emptyResult;
        private final List<String> equipmentIdList;

        private EquipmentScope(boolean emptyResult, List<String> equipmentIdList) {
            this.emptyResult = emptyResult;
            this.equipmentIdList = equipmentIdList;
        }

        static EquipmentScope noFilter() {
            return new EquipmentScope(false, null);
        }

        static EquipmentScope empty() {
            return new EquipmentScope(true, Collections.emptyList());
        }

        static EquipmentScope of(List<String> equipmentIdList) {
            return new EquipmentScope(false, equipmentIdList);
        }

        boolean isEmptyResult() {
            return emptyResult;
        }

        boolean hasFilter() {
            return equipmentIdList != null;
        }

        List<String> getEquipmentIdList() {
            return equipmentIdList;
        }
    }
}
