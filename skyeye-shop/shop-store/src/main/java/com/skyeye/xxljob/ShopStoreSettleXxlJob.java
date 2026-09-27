/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.finance.service.ShopStoreAccountService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 门店收货冻结期满 → 可提现（单笔到点，配合 Quartz 延迟任务）
 * <p>扫漏见 {@link com.skyeye.finance.schedule.ShopStoreSettleSchedule}</p>
 */
@Component
public class ShopStoreSettleXxlJob {

    private static final Logger log = LoggerFactory.getLogger(ShopStoreSettleXxlJob.class);

    @Autowired
    private ShopStoreAccountService shopStoreAccountService;

    @Autowired
    private IQuartzService iQuartzService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 单笔到点结算。JobParam 含 objectId=orderItemId；可选 tenantId。
     */
    @XxlJob("releaseShopStoreSettleByItem")
    public void releaseShopStoreSettleByItem() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String orderItemId = paramMap == null ? null : paramMap.get("objectId");
        String tenantId = tenantEnable && paramMap != null ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable && StrUtil.isNotBlank(tenantId)) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            log.info("门店结算到点开始 itemId={}", orderItemId);
            shopStoreAccountService.releaseSettlementByOrderItemId(orderItemId);
            log.info("门店结算到点结束 itemId={}", orderItemId);
        } finally {
            if (StrUtil.isNotBlank(orderItemId)) {
                try {
                    iQuartzService.stopAndDeleteTaskQuartz(orderItemId);
                } catch (Exception e) {
                    log.warn("删除门店结算任务失败 itemId={}", orderItemId, e);
                }
            }
        }
    }
}
