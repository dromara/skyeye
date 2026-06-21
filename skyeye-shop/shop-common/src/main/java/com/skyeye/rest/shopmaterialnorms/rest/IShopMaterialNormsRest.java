/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shopmaterialnorms.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @ClassName: IShopMaterialNormsRest
 * @Description: ERP商城购物车信息管理公共的一些操作
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 10:32
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IShopMaterialNormsRest {

    @PostMapping("/queryShopMaterialByNormsIdList")
    String queryShopMaterialByNormsIdList(@RequestParam("normsIds") String normsIds);

    @PostMapping("/queryShopMaterialByMaterialIdList")
    String queryShopMaterialByMaterialIdList(@RequestParam("materialIds") String materialIds);

    /**
     * 根据id批量获取商城商品信息
     *
     * @param ids 主键id，多个逗号隔开
     * @return
     */
    @PostMapping("/queryShopMaterialByIds")
    String queryShopMaterialByIds(@RequestParam("ids") String ids);

    /**
     * 新增门店时，将所有商品同步到该门店
     *
     * @param storeId 门店id
     * @return
     */
    @PostMapping("/saveShopMaterialStore")
    String saveShopMaterialStore(@RequestParam("storeId") String storeId);

    /**
     * 根据门店id删除商城商品信息
     *
     * @param storeIds 门店id，多个逗号隔开
     * @return
     */
    @PostMapping("/deleteShopMaterialStoreByStoreIds")
    String deleteShopMaterialStoreByStoreIds(@RequestParam("storeIds") String storeIds);

    /**
     * 获取商城商品信息列表供选择
     *
     * @return
     */
    @GetMapping("/queryAllShopMaterialListForChoose")
    String queryAllShopMaterialListForChoose();

}
