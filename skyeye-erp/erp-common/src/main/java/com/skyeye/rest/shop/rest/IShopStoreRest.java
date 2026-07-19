/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shop.rest;

import com.skyeye.common.client.ClientConfiguration;
import com.skyeye.common.entity.search.CommonPageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName: IShopStoreRest
 * @Description: 门店信息管理公共的一些操作
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 10:32
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@FeignClient(value = "${webroot.skyeye-shop}", configuration = ClientConfiguration.class)
public interface IShopStoreRest {

    /**
     * 根据id批量获取门店信息
     *
     * @param ids 主键id，多个用逗号隔开
     */
    @PostMapping("/queryStoreByIds")
    String queryStoreByIds(@RequestParam("ids") String ids);

    /**
     * 获取门店列表信息
     *
     * @param shopAreaId 门店所属区域id
     * @param enabled    是否启用
     */
    @GetMapping("/queryStoreListByParams")
    String queryStoreListByParams(@RequestParam("shopAreaId") String shopAreaId,
                                  @RequestParam("enabled") Integer enabled);

    /**
     * 分页获取门店列表信息
     */
    @PostMapping("/queryStoreListFoServer")
    String queryStoreListFoServer(CommonPageInfo commonPageInfo);

    /**
     * 根据id获取优惠券信息（含适用商品、门店）
     *
     * @param id 优惠券id
     */
    @PostMapping("/queryCouponById")
    String queryCouponById(@RequestParam("id") String id);

}
