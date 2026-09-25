/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.browse.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.browse.entity.ShopStoreBrowseStatDaily;

import java.util.List;
import java.util.Map;

public interface ShopStoreBrowseStatDailyService extends SkyeyeBusinessService<ShopStoreBrowseStatDaily> {

    /**
     * 汇总前一日各门店访客/浏览量并写入日表，同时清理超过半年的汇总数据
     */
    void aggregateYesterdayAndClean();

    /**
     * 查询指定门店在日期区间内的汇总（含起止日）
     */
    Map<String, Object> sumByStoreAndDateRange(String storeId, String fromDate, String toDate);

    /**
     * 查询指定门店在日期区间内的按日明细（含起止日）
     */
    List<Map<String, Object>> listDailyByStoreAndDateRange(String storeId, String fromDate, String toDate);
}
