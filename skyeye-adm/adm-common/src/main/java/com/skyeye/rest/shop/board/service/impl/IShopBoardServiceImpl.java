/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shop.board.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.shop.board.service.IShopBoardService;
import com.skyeye.rest.shop.order.rest.IShopOrderRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 买家 queryMyOrderStat + 可选门店 queryStoreOrderStat（store_ 前缀避免与买家字段冲突）。
 */
@Service
public class IShopBoardServiceImpl implements IShopBoardService {

    @Autowired
    private IShopOrderRest iShopOrderRest;

    @Override
    public Map<String, Object> queryMallBoardCounts(String storeId) {
        Map<String, Object> counts = new LinkedHashMap<>();
        Map<String, Object> buyer = safeBean(() -> iShopOrderRest.queryMyOrderStat(new HashMap<>()));
        putBuyer(counts, buyer);
        if (StrUtil.isNotBlank(storeId)) {
            Map<String, Object> params = new HashMap<>();
            params.put("objectId", storeId.trim());
            Map<String, Object> store = safeBean(() -> iShopOrderRest.queryStoreOrderStat(params));
            putStore(counts, store);
        }
        return counts;
    }

    private void putBuyer(Map<String, Object> counts, Map<String, Object> bean) {
        if (CollectionUtil.isEmpty(bean)) {
            return;
        }
        counts.put("waitPay", bean.getOrDefault("waitPay", 0));
        counts.put("waitDeliver", bean.getOrDefault("waitDeliver", 0));
        counts.put("waitReceive", bean.getOrDefault("waitReceive", 0));
        counts.put("waitReview", bean.getOrDefault("waitReview", 0));
        counts.put("afterSale", bean.getOrDefault("afterSale", 0));
    }

    private void putStore(Map<String, Object> counts, Map<String, Object> bean) {
        if (CollectionUtil.isEmpty(bean)) {
            return;
        }
        counts.put("store_waitPay", bean.getOrDefault("waitPay", 0));
        counts.put("store_waitDeliver", bean.getOrDefault("waitDeliver", 0));
        counts.put("store_waitReceive", bean.getOrDefault("waitReceive", 0));
        counts.put("store_afterSale", bean.getOrDefault("afterSale", 0));
        counts.put("store_todayOrder", bean.getOrDefault("todayOrder", 0));
        counts.put("store_todayAmount", bean.getOrDefault("todayAmount", "0"));
    }

    private Map<String, Object> safeBean(Supplier<String> loader) {
        try {
            ResultEntity entity = ExecuteFeignClient.get(loader);
            if (entity == null || CollectionUtil.isEmpty(entity.getBean())) {
                return Collections.emptyMap();
            }
            return entity.getBean();
        } catch (CustomException e) {
            String msg = e.getMessage();
            if (StrUtil.containsIgnoreCase(msg, "登录") || StrUtil.containsIgnoreCase(msg, "TIMEOUT")
                || StrUtil.containsIgnoreCase(msg, "凭证")) {
                throw e;
            }
            // 门店不存在等：跳过该段，保留买家统计
            return Collections.emptyMap();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
