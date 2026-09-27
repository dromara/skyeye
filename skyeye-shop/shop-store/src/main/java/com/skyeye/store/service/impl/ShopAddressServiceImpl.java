/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAreaService;
import com.skyeye.store.dao.ShopAddressDao;
import com.skyeye.store.entity.ShopAddress;
import com.skyeye.store.service.ShopAddressLabelService;
import com.skyeye.store.service.ShopAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopAddressServiceImpl
 * @Description: 收件地址管理服务层--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "收件地址管理", groupName = "收件地址管理", tenant = TenantEnum.NO_ISOLATION)
public class ShopAddressServiceImpl extends SkyeyeBusinessServiceImpl<ShopAddressDao, ShopAddress> implements ShopAddressService {

    @Autowired
    private IAreaService iAreaService;

    @Autowired
    private ShopAddressLabelService shopAddressLabelService;

    @Override
    public void writePostpose(ShopAddress shopAddress, String userId) {
        if (WhetherEnum.ENABLE_USING.getKey().equals(shopAddress.getIsDefault())) {
            UpdateWrapper<ShopAddress> updateWrapper = new UpdateWrapper<>();
            updateWrapper.ne(CommonConstants.ID, shopAddress.getId());
            updateWrapper.eq(MybatisPlusUtil.toColumns(ShopAddress::getCreateId), userId);
            updateWrapper.eq(MybatisPlusUtil.toColumns(ShopAddress::getIsDefault), WhetherEnum.ENABLE_USING.getKey());
            ShopAddress one = getOne(updateWrapper);
            if (ObjectUtil.isEmpty(one)) {
                return;
            }
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopAddress::getIsDefault), WhetherEnum.DISABLE_USING.getKey());
            update(updateWrapper);
            refreshCache(one.getId());
        }
    }

    @Override
    @IgnoreTenant
    public ShopAddress selectById(String id) {
        ShopAddress shopAddress = super.selectById(id);
        iAreaService.setDataMation(shopAddress, ShopAddress::getProvinceId);
        iAreaService.setDataMation(shopAddress, ShopAddress::getCityId);
        iAreaService.setDataMation(shopAddress, ShopAddress::getAreaId);
        iAreaService.setDataMation(shopAddress, ShopAddress::getTownshipId);
        shopAddressLabelService.setDataMation(shopAddress, ShopAddress::getLabelId);
        return shopAddress;
    }

    @Override
    public void queryDefaultShopAddress(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<ShopAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopAddress::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopAddress::getIsDefault), WhetherEnum.ENABLE_USING.getKey());
        ShopAddress one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            return;
        }
        iAreaService.setDataMation(one, ShopAddress::getProvinceId);
        iAreaService.setDataMation(one, ShopAddress::getCityId);
        iAreaService.setDataMation(one, ShopAddress::getAreaId);
        iAreaService.setDataMation(one, ShopAddress::getTownshipId);
        shopAddressLabelService.setDataMation(one, ShopAddress::getLabelId);
        outputObject.setBean(one);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public Map<String, Map<String, Object>> queryListByIds(List<String> addressTableIdList) {
        List<ShopAddress> list = selectByIds(addressTableIdList.toArray(new String[]{}));
        iAreaService.setDataMation(list, ShopAddress::getProvinceId);
        iAreaService.setDataMation(list, ShopAddress::getCityId);
        iAreaService.setDataMation(list, ShopAddress::getAreaId);
        iAreaService.setDataMation(list, ShopAddress::getTownshipId);
        shopAddressLabelService.setDataMation(list, ShopAddress::getLabelId);
        Map<String, Map<String, Object>> result = list.stream().collect(
            Collectors.toMap(ShopAddress::getId, shopAddress -> JSONUtil.toBean(JSONUtil.toJsonStr(shopAddress), null), (key1, key2) -> key2));
        return result;
    }

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<ShopAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopAddress::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ShopAddress::getCreateTime));
        List<ShopAddress> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        iAreaService.setDataMation(list, ShopAddress::getProvinceId);
        iAreaService.setDataMation(list, ShopAddress::getCityId);
        iAreaService.setDataMation(list, ShopAddress::getAreaId);
        iAreaService.setDataMation(list, ShopAddress::getTownshipId);
        shopAddressLabelService.setDataMation(list, ShopAddress::getLabelId);
        return JSONUtil.toList(JSONUtil.toJsonStr(list), null);
    }
}
