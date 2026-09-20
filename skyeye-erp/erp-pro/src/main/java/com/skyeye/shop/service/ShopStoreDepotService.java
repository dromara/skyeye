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

    void switchPersonalStoreStockMode(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreInventoryDiagnosis(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreInventoryHome(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreMaterialStockEdit(InputObject inputObject, OutputObject outputObject);

    List<ShopStoreDepot> listEnabledByStoreId(String storeId);
}
