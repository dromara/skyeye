/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.collect.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.collect.dao.MemberGoodsCollectDao;
import com.skyeye.collect.entity.MemberGoodsCollect;
import com.skyeye.collect.service.MemberGoodsCollectService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "会员商品收藏", groupName = "会员管理", tenant = TenantEnum.NO_ISOLATION)
public class MemberGoodsCollectServiceImpl extends SkyeyeBusinessServiceImpl<MemberGoodsCollectDao, MemberGoodsCollect> implements MemberGoodsCollectService {

    @Autowired
    private IShopMaterialNormsService iShopMaterialNormsService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Override
    public void toggleGoodsCollect(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String memberId = MapUtil.getStr(inputObject.getLogParams(), CommonConstants.ID);
        String materialStoreId = MapUtil.getStr(params, "materialStoreId");
        QueryWrapper<MemberGoodsCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberGoodsCollect::getMemberId), memberId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberGoodsCollect::getMaterialStoreId), materialStoreId);
        MemberGoodsCollect exist = getOne(queryWrapper, false);

        Map<String, Object> result = new HashMap<>();
        if (exist != null) {
            // 取消收藏
            deleteById(exist.getId());
            result.put("collected", false);
            outputObject.setBean(result);
            return;
        }
        // 收藏
        MemberGoodsCollect collect = new MemberGoodsCollect();
        collect.setMemberId(memberId);
        collect.setMaterialStoreId(materialStoreId);
        createEntity(collect, memberId);
        result.put("collected", true);
        outputObject.setBean(result);
    }

    @Override
    protected QueryWrapper<MemberGoodsCollect> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<MemberGoodsCollect> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String memberId = MapUtil.getStr(InputObject.getLogParamsStatic(), CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberGoodsCollect::getMemberId), memberId);
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        setCollectMation(beans);
        return beans;
    }

    /**
     * 列表展示信息：批量回填商城商品、门店（与订单子单 setDateForItemLIst 同一套）
     */
    private void setCollectMation(List<Map<String, Object>> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        // 获取门店商品关联id
        List<String> materialStoreIds = list.stream().map(item -> item.get("materialStoreId").toString())
            .distinct().collect(Collectors.toList());
        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);
        Map<String, Map<String, Object>> materialStoreMap = materialByIds.stream()
            .distinct().collect(Collectors.toMap(map -> {
                Map<String, Object> shopMaterialStore = JSONUtil.toBean(map.get("shopMaterialStore").toString(), null);
                return shopMaterialStore.get("id").toString();
            }, map -> map));
        list.forEach(item -> {
            Map<String, Object> materialStoreObject = materialStoreMap.get(item.get("materialStoreId").toString());
            item.put("shopMaterial", materialStoreObject);
            Map<String, Object> shopMaterialStore = JSONUtil.toBean(materialStoreObject.get("shopMaterialStore").toString(), null);
            item.put("storeId", (shopMaterialStore.get("storeId").toString()));
        });
        shopStoreService.setMationForMap(list, "storeId", "storeMation");
    }

    @Override
    public void checkGoodsCollect(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String memberId = MapUtil.getStr(inputObject.getLogParams(), CommonConstants.ID);
        String materialStoreId = MapUtil.getStr(params, "materialStoreId");
        QueryWrapper<MemberGoodsCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberGoodsCollect::getMemberId), memberId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberGoodsCollect::getMaterialStoreId), materialStoreId);
        MemberGoodsCollect exist = getOne(queryWrapper, false);
        Map<String, Object> result = new HashMap<>();
        result.put("collected", exist != null);
        outputObject.setBean(result);
    }

}
