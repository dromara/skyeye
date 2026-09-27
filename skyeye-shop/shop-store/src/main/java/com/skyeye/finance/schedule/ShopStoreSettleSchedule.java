/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.finance.schedule;

import com.skyeye.finance.service.ShopStoreAccountService;
import com.skyeye.jedis.util.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 门店收货冻结期满扫漏（资金账本不隔离租户，无需按租户循环）
 * <p>每小时整点扫描，将到期冻结转入可提现；单笔到点仍走 Quartz 延迟任务。</p>
 * <p>多实例部署时靠 Redis 锁保证同一时刻仅一台执行。</p>
 */
@Component
public class ShopStoreSettleSchedule {

    private static final Logger log = LoggerFactory.getLogger(ShopStoreSettleSchedule.class);

    /** 与往来统计等定时任务同套路：集群下只跑一台 */
    private static final String LOCK_KEY = "releaseShopStoreSettleScan";

    @Autowired
    private ShopStoreAccountService shopStoreAccountService;

    @Scheduled(cron = "0 0 * * * ?")
    public void releaseShopStoreSettleScan() {
        // 等锁 1s：抢不到说明别的实例在跑，直接跳过；持锁最长 10 分钟防扫漏耗时过久
        RedisLock lock = new RedisLock(LOCK_KEY, 1000, 10 * 60 * 1000);
        try {
            if (!lock.lock()) {
                log.info("门店结算扫漏定时任务 未获取到锁，跳过");
                return;
            }
            log.info("门店结算扫漏定时任务 start");
            shopStoreAccountService.releaseAllDueSettlements();
            log.info("门店结算扫漏定时任务 end");
        } catch (Exception e) {
            log.warn("门店结算扫漏定时任务 error.", e);
        } finally {
            lock.unlock();
        }
    }
}
