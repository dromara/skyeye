/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.knowledge.entity.KnowledgeSyncHistoryItem;

import java.util.List;
import java.util.Map;

public interface KnowledgeSyncHistoryItemService extends SkyeyeBusinessService<KnowledgeSyncHistoryItem> {

    void saveItems(String historyId, List<KnowledgeSyncHistoryItem> items);

    /** 给历史列表附带表/文件数量汇总，不返回全量明细 */
    void setItemSummaryForMap(List<Map<String, Object>> beans);

    void deleteByHistoryIds(List<String> historyIds);

}
