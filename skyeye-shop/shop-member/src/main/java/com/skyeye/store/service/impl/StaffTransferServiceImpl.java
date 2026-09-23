/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.store.dao.StaffTransferDao;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.entity.StaffTransfer;
import com.skyeye.store.service.ShopStoreService;
import com.skyeye.store.service.ShopStoreStaffService;
import com.skyeye.store.service.StaffTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: StaffTransferServiceImpl
 * @Description: 员工调拨申请服务层
 * @author: skyeye云系列--卫志强
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "员工调拨申请", groupName = "员工调拨申请", flowable = true)
public class StaffTransferServiceImpl extends SkyeyeBusinessServiceImpl<StaffTransferDao, StaffTransfer> implements StaffTransferService {

    @Autowired
    private ShopStoreStaffService shopStoreStaffService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Override
    protected QueryWrapper<StaffTransfer> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<StaffTransfer> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(StaffTransfer::getCreateId), userId);
        String storeId = commonPageInfo.getCustomParamsMapStr("storeId");
        queryWrapper.eq(MybatisPlusUtil.toColumns(StaffTransfer::getFromStoreId), storeId);
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置员工信息和门店信息
        setStaffAndStoreMation(beans);
        return beans;
    }

    @Override
    public StaffTransfer selectById(String id) {
        StaffTransfer staffTransfer = super.selectById(id);
        // 设置员工信息
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(Arrays.asList(staffTransfer.getStaffId()));
        staffTransfer.setStaffMation(staffMap.get(staffTransfer.getStaffId()));
        // 设置门店信息
        ShopStore fromStore = shopStoreService.selectById(staffTransfer.getFromStoreId());
        if (fromStore != null) {
            staffTransfer.setFromStoreMation(fromStore);
        }
        ShopStore toStore = shopStoreService.selectById(staffTransfer.getToStoreId());
        if (toStore != null) {
            staffTransfer.setToStoreMation(toStore);
        }
        // 设置状态名称
        staffTransfer.setStateName(FlowableStateEnum.getStateName(staffTransfer.getState()));
        iAuthUserService.setName(staffTransfer, "createId", "createName");
        return staffTransfer;
    }

    /**
     * 设置员工信息和门店信息
     */
    private void setStaffAndStoreMation(List<Map<String, Object>> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        // 设置员工信息
        List<String> staffIds = beans.stream()
            .map(bean -> bean.get("staffId").toString())
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(staffIds)) {
            Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
            beans.forEach(bean -> {
                bean.put("staffMation", staffMap.get(bean.get("staffId").toString()));
            });
        }
        // 设置门店信息
        List<String> storeIds = beans.stream()
            .flatMap(bean -> {
                List<String> ids = new java.util.ArrayList<>();
                ids.add(bean.get("fromStoreId").toString());
                ids.add(bean.get("toStoreId").toString());
                return ids.stream();
            }).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(storeIds)) {
            List<ShopStore> shopStores = shopStoreService.selectByIds(storeIds.toArray(new String[]{}));
            Map<String, ShopStore> storeMap = shopStores.stream()
                .collect(Collectors.toMap(ShopStore::getId, shopStore -> shopStore));
            beans.forEach(bean -> {
                bean.put("fromStoreMation", storeMap.get(bean.get("fromStoreId").toString()));
                bean.put("toStoreMation", storeMap.get(bean.get("toStoreId").toString()));
            });
        }
    }

    @Override
    protected void approvalEndIsSuccess(StaffTransfer entity) {
        // 审批通过后，执行员工调拨
        shopStoreStaffService.executeStaffTransfer(entity.getStaffId(), entity.getFromStoreId(), entity.getToStoreId());
    }

}

