/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.exception.CustomException;
import com.skyeye.store.classenum.StoreNature;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.entity.ShopStoreStaff;
import com.skyeye.store.service.ShopStoreService;
import com.skyeye.store.service.ShopStoreStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 门店装修权限：店主 / 门店员工 / 非个人店管理端可操作
 */
@Component
public class StoreDecorateAuthHelper {

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private ShopStoreStaffService shopStoreStaffService;

    public ShopStore assertStoreDecorateAccess(String storeId) {
        if (StrUtil.isBlank(storeId)) {
            throw new CustomException("请选择门店");
        }
        ShopStore store = shopStoreService.selectById(storeId);
        if (store == null || StrUtil.isBlank(store.getId())) {
            throw new CustomException("门店不存在");
        }
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        if (userId.equals(store.getCreateId())) {
            return store;
        }
        Object staffIdObj = InputObject.getLogParamsStatic().get("staffId");
        String staffId = staffIdObj == null ? StrUtil.EMPTY : staffIdObj.toString();
        if (StrUtil.isNotBlank(staffId) && !"tmpUserStaffId".equals(staffId) && isStoreStaff(storeId, staffId)) {
            return store;
        }
        if (!StoreNature.PERSONAL.getKey().equals(store.getStoreNature())) {
            return store;
        }
        throw new CustomException("无权操作该门店");
    }

    private boolean isStoreStaff(String storeId, String staffId) {
        List<ShopStoreStaff> staffList = shopStoreStaffService.getShopStoresByStoreId(storeId);
        if (CollectionUtil.isEmpty(staffList)) {
            return false;
        }
        return staffList.stream().anyMatch(s -> staffId.equals(s.getStaffId()));
    }
}
