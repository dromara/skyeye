/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shop.service;

import com.skyeye.base.rest.service.IService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.ResultEntity;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IShopStoreService
 * @Description: 门店信息管理公共的一些操作
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 10:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface IShopStoreService extends IService {

    List<Map<String, Object>> queryStoreListByParams(String shopAreaId, Integer enabled);

    ResultEntity queryStoreListFoServer(CommonPageInfo commonPageInfo);

    /**
     * 查询优惠券适用商品/门店范围
     *
     * @param couponId 优惠券id
     * @return allMaterial、materialIds、allStore、storeIds
     */
    Map<String, Object> queryCouponApplicableScope(String couponId);

}
