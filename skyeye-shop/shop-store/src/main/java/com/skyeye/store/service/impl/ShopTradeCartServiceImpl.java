/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.dao.ShopTradeCartDao;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.entity.ShopTradeCart;
import com.skyeye.store.service.ShopStoreService;
import com.skyeye.store.service.ShopTradeCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopTradeCartServiceImpl
 * @Description: 购物车管理服务层--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:07
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "购物车管理", groupName = "购物车管理", tenant = TenantEnum.NO_ISOLATION)
public class ShopTradeCartServiceImpl extends SkyeyeBusinessServiceImpl<ShopTradeCartDao, ShopTradeCart> implements ShopTradeCartService {

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private IShopMaterialNormsService iShopMaterialNormsService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Override
    public void validatorEntity(ShopTradeCart shopTradeCart) {
        super.validatorEntity(shopTradeCart);
        if (shopTradeCart.getCount() <= CommonNumConstants.NUM_ZERO) {
            throw new CustomException("商品数量不能小于1");
        }
    }

    @Override
    public void queryShopTradeCartList(InputObject inputObject, OutputObject outputObject) {
        String selected = inputObject.getParams().get("selected").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 查询用户购物车列表
        QueryWrapper<ShopTradeCart> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(ShopTradeCart::getCreateId), userId);
        if (StrUtil.isNotEmpty(selected)) {
            wrapper.eq(MybatisPlusUtil.toColumns(ShopTradeCart::getSelected), selected);
        }
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(ShopTradeCart::getCreateTime));
        List<ShopTradeCart> beans = list(wrapper);
        iMaterialNormsService.setDataMation(beans, ShopTradeCart::getNormsId);
        if (CollectionUtil.isNotEmpty(beans)) {
            // 收集规格id列表，获得规格信息
            List<String> normsIdList = beans.stream().map(ShopTradeCart::getNormsId).collect(Collectors.toList());
            List<Map<String, Object>> normsListMap = iShopMaterialNormsService
                .queryShopMaterialByNormsIdList(Joiner.on(CommonCharConstants.COMMA_MARK).join(normsIdList));
            // 设置商城的销售价格
            Map<String, String> collect = normsListMap.stream()
                .collect(Collectors.toMap(bean -> bean.get("normsId").toString(), bean -> bean.get("salePrice").toString()));

            // 设置商城商品信息
            List<String> materialIdList = beans.stream().map(ShopTradeCart::getMaterialId).distinct().collect(Collectors.toList());
            List<Map<String, Object>> shopMaterialList = iShopMaterialNormsService
                .queryShopMaterialByMaterialIdList(Joiner.on(CommonCharConstants.COMMA_MARK).join(materialIdList));
            Map<String, Map<String, Object>> shopMaterialListMap = shopMaterialList.stream()
                .collect(Collectors.toMap(bean -> bean.get("materialId").toString(), bean -> bean, (a, b) -> a));
            beans.forEach(bean -> {
                String normsId = bean.getNormsId();
                String salePrice = collect.get(normsId);
                if (CollectionUtil.isNotEmpty(bean.getNormsMation())) {
                    bean.getNormsMation().put("salePrice", salePrice);
                }
                bean.setShopMaterialMation(shopMaterialListMap.get(bean.getMaterialId()));
            });
        }
        iMaterialService.setDataMation(beans, ShopTradeCart::getMaterialId);
        // 查询店铺信息
        List<String> storeIdList = beans.stream().map(ShopTradeCart::getStoreId).collect(Collectors.toList());
        List<ShopStore> shopStoreList = shopStoreService.selectByIds(storeIdList.toArray(new String[]{}));
        Map<String, Object> shopStoreMap = shopStoreList.stream().collect(Collectors.toMap(ShopStore::getId, ShopStore::getName));
        outputObject.setBeans(beans);
        outputObject.setCustomBean("AllShopStoreInfo", shopStoreMap);
    }

    @Override
    public String createEntity(ShopTradeCart shopTradeCart, String userId) {
        QueryWrapper<ShopTradeCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopTradeCart::getStoreId), shopTradeCart.getStoreId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopTradeCart::getMaterialId), shopTradeCart.getMaterialId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopTradeCart::getNormsId), shopTradeCart.getNormsId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopTradeCart::getCreateId), userId);
        ShopTradeCart one = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(one)) {
            shopTradeCart.setId(one.getId());
            shopTradeCart.setCount(one.getCount() + shopTradeCart.getCount());
            return super.updateEntity(shopTradeCart, userId);
        }
        return super.createEntity(shopTradeCart, userId);
    }

    @Override
    public void changeSelected(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String id = inputObject.getParams().get("id").toString();
        UpdateWrapper<ShopTradeCart> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        ShopTradeCart one = getOne(updateWrapper);
        if (!userId.equals(one.getCreateId())) {
            throw new CustomException("无权限!");
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopTradeCart::getSelected),
            Objects.equals(one.getSelected(), WhetherEnum.ENABLE_USING.getKey())
                ? WhetherEnum.DISABLE_USING.getKey() : WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public void batchChangeSelectedStatus(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String idsStr = params.get("ids").toString();
        List<String> ids = Arrays.stream(idsStr.split(CommonCharConstants.COMMA_MARK)).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Integer selected = Integer.parseInt(params.get("selected").toString());
        UpdateWrapper<ShopTradeCart> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(CommonConstants.ID, ids);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopTradeCart::getCreateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopTradeCart::getSelected), selected);
        update(updateWrapper);
    }

    @Override
    public void deleteMySelect(String userId) {
        QueryWrapper<ShopTradeCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopTradeCart::getSelected), WhetherEnum.ENABLE_USING.getKey())
            .eq(MybatisPlusUtil.toColumns(ShopTradeCart::getCreateId), userId);
        remove(queryWrapper);
    }

    @Override
    public void changeCount(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Integer sign = Integer.parseInt(params.get("sign").toString());
        UpdateWrapper<ShopTradeCart> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        ShopTradeCart one = getOne(updateWrapper);
        Integer count = one.getCount();
        if (Objects.equals(sign, CommonNumConstants.NUM_ONE)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopTradeCart::getCount), count + CommonNumConstants.NUM_ONE);
        } else {
            if (count <= CommonNumConstants.NUM_ONE) {
                throw new CustomException("商品数量不能小于1");
            }
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopTradeCart::getCount), count - CommonNumConstants.NUM_ONE);
        }
        update(updateWrapper);
    }

    @Override
    public void resetShopTradeCart(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<ShopTradeCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopTradeCart::getCreateId), userId);
        remove(queryWrapper);
    }

    @Override
    public void calculateTotalPrices(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<ShopTradeCart> wrapper = new QueryWrapper<>();
        wrapper.in(MybatisPlusUtil.toColumns(ShopTradeCart::getCreateId), userId);
        // 查询选中的商品
        wrapper.eq(MybatisPlusUtil.toColumns(ShopTradeCart::getSelected), WhetherEnum.ENABLE_USING.getKey());
        List<ShopTradeCart> beans = list(wrapper);
        //设置返回值
        Map<String, String> result = new HashMap<>();
        final String[] allPrice = {"0"};
        if (CollectionUtil.isNotEmpty(beans)) {
            // 遇到规格id相同的商品，就累加数量
            Map<String, Integer> countMap = beans.stream().collect(Collectors
                .toMap(ShopTradeCart::getNormsId, ShopTradeCart::getCount, Integer::sum));
            // 收集规格id列表，获得规格信息
            List<String> normsIdList = beans.stream().map(ShopTradeCart::getNormsId).collect(Collectors.toList());
            List<Map<String, Object>> normsListMap = iShopMaterialNormsService
                .queryShopMaterialByNormsIdList(Joiner.on(CommonCharConstants.COMMA_MARK).join(normsIdList));
            // 计算价格
            normsListMap.forEach(map -> {
                String id = map.get("normsId").toString();
                String count = StrUtil.toString(countMap.getOrDefault(id, CommonNumConstants.NUM_ZERO));
                String salePrice = map.get("salePrice").toString();
                String flagPrice = CalculationUtil.multiply(count, salePrice, CommonNumConstants.NUM_TWO);
                allPrice[0] = CalculationUtil.add(allPrice[0], flagPrice);
            });
        }
        result.put("allPrice", Joiner.on(CommonCharConstants.COMMA_MARK).join(allPrice));
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}