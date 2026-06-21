/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shopstock.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @ClassName: IShopStockRest
 * @Description: ERP 门店库存（shop_stock）Feign 客户端
 */
@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IShopStockRest {

    /**
     * 执行门店产品库存调拨（对应 ERP ShopStockController.executeStoreProductTransfer）
     *
     * @param params 需包含 fromStoreId、toStoreId、materialId、normsId、operNumber
     */
    @PostMapping("/executeStoreProductTransfer")
    String executeStoreProductTransfer(Map<String, Object> params);

}
