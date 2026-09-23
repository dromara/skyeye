/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shopstock.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.shopstock.rest.IShopStockRest;
import com.skyeye.rest.shopstock.service.IShopStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: IShopStockServiceImpl
 * @Description: ERP 门店库存（shop_stock）Feign 公共操作
 */
@Service
public class IShopStockServiceImpl extends IServiceImpl implements IShopStockService {

    @Autowired
    private IShopStockRest iShopStockRest;

    @Override
    public void executeStoreProductTransfer(Map<String, Object> params) {
        ExecuteFeignClient.get(() -> iShopStockRest.executeStoreProductTransfer(params));
    }

    @Override
    public void deductShopStockOnShip(Map<String, Object> params) {
        // 转发至 ERP：按 stockMode 扣门店库存 / 商家仓规格库存
        ExecuteFeignClient.get(() -> iShopStockRest.deductShopStockOnShip(params));
    }

    @Override
    public void checkShopStockForSale(Map<String, Object> params) {
        ExecuteFeignClient.get(() -> iShopStockRest.checkShopStockForSale(params));
    }

}
