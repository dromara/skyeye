/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.shop.entity.ShopStoreDepot;

import java.util.List;

/**
 * @ClassName: ShopStoreDepotService
 * @Description: 个人门店仓库关联与库存服务
 */
public interface ShopStoreDepotService extends SkyeyeBusinessService<ShopStoreDepot> {

    void queryPersonalStoreDepotList(InputObject inputObject, OutputObject outputObject);

    void createPersonalStoreDepot(InputObject inputObject, OutputObject outputObject);

    void updatePersonalStoreDepot(InputObject inputObject, OutputObject outputObject);

    void setDefaultPersonalStoreDepot(InputObject inputObject, OutputObject outputObject);

    void deletePersonalStoreDepot(InputObject inputObject, OutputObject outputObject);

    void savePersonalStoreDepotPriority(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreInventoryList(InputObject inputObject, OutputObject outputObject);

    void adjustPersonalStoreInventory(InputObject inputObject, OutputObject outputObject);

    /**
     * 发货扣库存（供 Shop 侧 deliverGoodsById 经 Feign 调用）。
     * <p>
     * stockMode=1（普通）：扣减 shop_stock；库存不足直接抛错。<br>
     * stockMode=2（关联仓）：按商家仓 priority 升序依次扣减 ERP 规格库存，可跨仓；总量不足抛错。<br>
     * 未传 materialStoreId 或关系不存在时，默认按普通模式处理。
     * </p>
     */
    void deductShopStockOnShip(InputObject inputObject, OutputObject outputObject);

    void switchPersonalStoreStockMode(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreInventoryDiagnosis(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreInventoryHome(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreMaterialStockEdit(InputObject inputObject, OutputObject outputObject);

    List<ShopStoreDepot> listEnabledByStoreId(String storeId);
}
