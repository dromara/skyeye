/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.knowledge.classenum.KnowledgeSyncItemTypeEnum;
import com.skyeye.knowledge.dao.KnowledgeSyncHistoryItemDao;
import com.skyeye.knowledge.entity.KnowledgeSyncHistoryItem;
import com.skyeye.knowledge.service.KnowledgeSyncHistoryItemService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "AI知识库同步历史明细", groupName = "AI知识库", allowDynamicAttrKey = false)
public class KnowledgeSyncHistoryItemServiceImpl
    extends SkyeyeBusinessServiceImpl<KnowledgeSyncHistoryItemDao, KnowledgeSyncHistoryItem>
    implements KnowledgeSyncHistoryItemService {

    @Override
    protected QueryWrapper<KnowledgeSyncHistoryItem> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<KnowledgeSyncHistoryItem> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.apply("1 = 0");
            return queryWrapper;
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeSyncHistoryItem::getHistoryId), commonPageInfo.getObjectId());
        if (StrUtil.isNotBlank(commonPageInfo.getType())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeSyncHistoryItem::getItemType), commonPageInfo.getType());
        }
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(KnowledgeSyncHistoryItem::getCreateTime));
        return queryWrapper;
    }

    @Override
    public void saveItems(String historyId, List<KnowledgeSyncHistoryItem> items) {
        if (StrUtil.isBlank(historyId) || CollectionUtil.isEmpty(items)) {
            return;
        }
        for (KnowledgeSyncHistoryItem item : items) {
            item.setId(null);
            item.setHistoryId(historyId);
            if (item.getSyncCount() == null) {
                item.setSyncCount(0);
            }
        }
        createEntity(items, StrUtil.EMPTY);
    }

    @Override
    public void setItemSummaryForMap(List<Map<String, Object>> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> historyIds = beans.stream()
            .map(bean -> bean.get("id") == null ? StrUtil.EMPTY : bean.get("id").toString())
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(historyIds)) {
            return;
        }
        String historyIdCol = MybatisPlusUtil.toColumns(KnowledgeSyncHistoryItem::getHistoryId);
        String itemTypeCol = MybatisPlusUtil.toColumns(KnowledgeSyncHistoryItem::getItemType);
        String syncCountCol = MybatisPlusUtil.toColumns(KnowledgeSyncHistoryItem::getSyncCount);
        String statusCol = MybatisPlusUtil.toColumns(KnowledgeSyncHistoryItem::getStatus);
        QueryWrapper<KnowledgeSyncHistoryItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(historyIdCol, historyIds);
        queryWrapper.select(historyIdCol + " AS historyId", itemTypeCol + " AS itemType",
            "COUNT(1) AS itemCnt",
            "IFNULL(SUM(" + syncCountCol + "),0) AS syncCnt",
            "SUM(CASE WHEN " + statusCol + " = 1 THEN 1 ELSE 0 END) AS successCnt");
        queryWrapper.groupBy(historyIdCol, itemTypeCol);
        List<Map<String, Object>> stats = listMaps(queryWrapper);
        Map<String, Map<String, Object>> summaryMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(stats)) {
            for (Map<String, Object> row : stats) {
                String historyId = String.valueOf(mapVal(row, "historyId", "history_id"));
                Integer itemType = toInt(mapVal(row, "itemType", "item_type"));
                Map<String, Object> summary = summaryMap.computeIfAbsent(historyId, key -> emptySummary());
                int itemCnt = toInt(mapVal(row, "itemCnt", "item_cnt"));
                int syncCnt = toInt(mapVal(row, "syncCnt", "sync_cnt"));
                int successCnt = toInt(mapVal(row, "successCnt", "success_cnt"));
                if (KnowledgeSyncItemTypeEnum.TABLE.getKey().equals(itemType)) {
                    summary.put("tableCount", itemCnt);
                    summary.put("tableSyncCount", syncCnt);
                } else if (KnowledgeSyncItemTypeEnum.FILE.getKey().equals(itemType)) {
                    summary.put("fileCount", itemCnt);
                    summary.put("fileSuccessCount", successCnt);
                }
                summary.put("itemCount", toInt(summary.get("itemCount")) + itemCnt);
            }
        }
        for (Map<String, Object> bean : beans) {
            String id = bean.get("id") == null ? StrUtil.EMPTY : bean.get("id").toString();
            bean.putAll(summaryMap.getOrDefault(id, emptySummary()));
        }
    }

    private Map<String, Object> emptySummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("tableCount", 0);
        summary.put("tableSyncCount", 0);
        summary.put("fileCount", 0);
        summary.put("fileSuccessCount", 0);
        summary.put("itemCount", 0);
        return summary;
    }

    private Object mapVal(Map<String, Object> row, String... keys) {
        if (row == null || keys == null) {
            return null;
        }
        for (String key : keys) {
            if (row.containsKey(key) && row.get(key) != null) {
                return row.get(key);
            }
        }
        return null;
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void deleteByHistoryIds(List<String> historyIds) {
        if (CollectionUtil.isEmpty(historyIds)) {
            return;
        }
        QueryWrapper<KnowledgeSyncHistoryItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(KnowledgeSyncHistoryItem::getHistoryId), historyIds);
        remove(queryWrapper);
    }

}
