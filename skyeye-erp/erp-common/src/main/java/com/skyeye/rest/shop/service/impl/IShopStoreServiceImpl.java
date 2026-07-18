/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shop.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.rest.shop.rest.IShopStoreRest;
import com.skyeye.rest.shop.service.IShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName: IShopStoreServiceImpl
 * @Description: 门店信息管理公共的一些操作
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 10:32
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class IShopStoreServiceImpl extends IServiceImpl implements IShopStoreService {

    @Autowired
    private IShopStoreRest iShopStoreRest;

    @Override
    public Map<String, Object> queryEntityMationById(String id) {
        return queryEntityMationByIds(id).stream().findFirst().orElse(new HashMap<>());
    }

    @Override
    public List<Map<String, Object>> queryEntityMationByIds(String ids) {
        return ExecuteFeignClient.get(() -> iShopStoreRest.queryStoreByIds(ids)).getRows();
    }

    @Override
    public String queryCacheKeyById(String id) {
        return String.format(Locale.ROOT, "%s:%s", CacheConstants.SHOP_STORE_CACHE_KEY, id);
    }

    @Override
    public List<Map<String, Object>> queryStoreListByParams(String shopAreaId, Integer enabled) {
        return ExecuteFeignClient.get(() -> iShopStoreRest.queryStoreListByParams(shopAreaId, enabled)).getRows();
    }

    @Override
    public ResultEntity queryStoreListFoServer(CommonPageInfo commonPageInfo) {
        return ExecuteFeignClient.get(() -> iShopStoreRest.queryStoreListFoServer(commonPageInfo));
    }

    @Override
    public Map<String, Object> queryCouponById(String id) {
        ResultEntity resultEntity = ExecuteFeignClient.get(() -> iShopStoreRest.queryCouponById(id));
        if (resultEntity == null || resultEntity.getBean() == null) {
            return new HashMap<>();
        }
        return resultEntity.getBean();
    }
}
