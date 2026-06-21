/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.shop.entity.ShopStock;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopStockService
 * @Description: 门店物料库存信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/31 16:58
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ShopStockService extends SkyeyeBusinessService<ShopStock> {

    /**
     * 修改门店物料库存存量信息
     *
     * @param storeId    门店id
     * @param materialId 商品id
     * @param normsId    规格id
     * @param operNumber 变化数量
     * @param type       出入库类型，参考#DepotPutOutType
     */
    void updateShopStock(String storeId, String materialId, String normsId, String operNumber, int type);

    ShopStock queryShopStock(String storeId, String normsId);

    Map<String, String> queryNormsShopStock(String storeId, List<String> normsIds);

    void queryShopStockList(InputObject inputObject, OutputObject outputObject);

    /**
     * 执行门店产品库存调拨：原门店出库，目标门店入库（审批通过后调用，含库存校验）
     */
    void executeStoreProductTransfer(InputObject inputObject, OutputObject outputObject);
}
