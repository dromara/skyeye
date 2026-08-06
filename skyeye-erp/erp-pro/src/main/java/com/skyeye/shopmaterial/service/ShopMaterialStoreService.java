/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.shopmaterial.entity.ShopMaterialStore;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopMaterialStoreService
 * @Description: 商城商品上线的门店服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/18 14:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ShopMaterialStoreService extends SkyeyeBusinessService<ShopMaterialStore> {

    void deleteByMaterialId(String materialId);

    List<ShopMaterialStore> selectByMaterialId(String materialId);

    Map<String, List<ShopMaterialStore>> selectByMaterialId(List<String> materialId);

    List<ShopMaterialStore> selectByStoreId(String storeId, Integer isLaunchStore, Integer isLunchShop, String keyword);

    /**
     * 添加指定商品到所有门店
     *
     * @param materialId 商品ID
     * @param bigTypeId
     */
    void addAllStoreForMaterial(String materialId, Integer storeCoverage, List<String> storeIds, String bigTypeId);

    void saveShopMaterialStore(InputObject inputObject, OutputObject outputObject);

    List<ShopMaterialStore> queryShopMaterialList(InputObject inputObject, OutputObject outputObject);

    Map<String, ShopMaterialStore> queryShopMaterialStoreByMaterialIds(String... materialIds);

    void queryShopMaterialById(InputObject inputObject, OutputObject outputObject);

    void queryShopMaterialByMaterialIdAndStoreId(InputObject inputObject, OutputObject outputObject);

    Map<String, List<ShopMaterialStore>> queryShopMaterialListByStoreIds(List<String> storeIds, CommonPageInfo commonPageInfo);

    void queryShopMaterialByIds(InputObject inputObject, OutputObject outputObject);

    void queryShopMaterialMapByMaterialIdAndStoreId(InputObject inputObject, OutputObject outputObject);

    /**
     * 按商品id（可选再限门店id）查询已添加门店、已上架商城、门店启用的去重门店id列表
     */
    void queryLaunchedStoreIdsByMaterialId(InputObject inputObject, OutputObject outputObject);

    void deleteShopMaterialStoreByStoreIds(InputObject inputObject, OutputObject outputObject);

    void addShopMaterialStore(InputObject inputObject, OutputObject outputObject);

    void deleteShopMaterialStore(InputObject inputObject, OutputObject outputObject);

    void launchShopMaterialStore(InputObject inputObject, OutputObject outputObject);

    void unlaunchShopMaterialStore(InputObject inputObject, OutputObject outputObject);

    void getAllowedShopMaterialList(InputObject inputObject, OutputObject outputObject);

    void getAddedShopMaterialList(InputObject inputObject, OutputObject outputObject);

    void getLaunchedShopMaterialList(InputObject inputObject, OutputObject outputObject);
}
