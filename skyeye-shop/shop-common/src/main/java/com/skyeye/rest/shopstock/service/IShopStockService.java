/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shopstock.service;

import com.skyeye.base.rest.service.IService;

import java.util.Map;

/**
 * @ClassName: IShopStockService
 * @Description: ERP 门店库存（shop_stock）Feign 公共操作
 */
public interface IShopStockService extends IService {

    /**
     * 执行门店产品库存调拨
     *
     * @param params 需包含 fromStoreId、toStoreId、applyLinkList（JSON 数组）
     */
    void executeStoreProductTransfer(Map<String, Object> params);

}
