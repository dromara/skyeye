/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.browse.schedule;

import com.skyeye.browse.service.ShopStoreBrowseStatDailyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 门店浏览日汇总定时任务（浏览足迹不隔离租户，无需按租户循环）
 * <p>每天 01:10 汇总前一日访客/浏览量，并清理超过半年的日汇总数据</p>
 */
@Component
public class StoreBrowseStatDailySchedule {

    private static final Logger log = LoggerFactory.getLogger(StoreBrowseStatDailySchedule.class);

    @Autowired
    private ShopStoreBrowseStatDailyService shopStoreBrowseStatDailyService;

    @Scheduled(cron = "0 10 1 * * ?")
    public void aggregateYesterdayAndClean() {
        log.info("门店浏览日汇总定时任务 start");
        try {
            shopStoreBrowseStatDailyService.aggregateYesterdayAndClean();
        } catch (Exception e) {
            log.warn("门店浏览日汇总定时任务 error.", e);
        }
        log.info("门店浏览日汇总定时任务 end");
    }
}
