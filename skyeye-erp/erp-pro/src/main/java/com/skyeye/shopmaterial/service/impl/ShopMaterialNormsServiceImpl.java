/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.shopmaterial.dao.ShopMaterialNormsDao;
import com.skyeye.shopmaterial.entity.ShopMaterialNorms;
import com.skyeye.shopmaterial.service.ShopMaterialNormsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopMaterialNormsServiceImpl
 * @Description: 商城商品规格参数服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/4 17:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商城商品规格", groupName = "商城商品规格", manageShow = false)
public class ShopMaterialNormsServiceImpl extends SkyeyeBusinessServiceImpl<ShopMaterialNormsDao, ShopMaterialNorms> implements ShopMaterialNormsService {

    @Override
    public void deleteByMaterialId(String materialId) {
        QueryWrapper<ShopMaterialNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialNorms::getMaterialId), materialId);
        remove(queryWrapper);
    }

    @Override
    @IgnoreTenant
    public List<ShopMaterialNorms> selectByMaterialId(String materialId) {
        QueryWrapper<ShopMaterialNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialNorms::getMaterialId), materialId);
        List<ShopMaterialNorms> shopMaterialNormsList = list(queryWrapper);
        return shopMaterialNormsList;
    }

    @Override
    @IgnoreTenant
    public Map<String, List<ShopMaterialNorms>> selectByMaterialId(List<String> materialId) {
        if (CollectionUtil.isEmpty(materialId)) {
            return MapUtil.empty();
        }
        QueryWrapper<ShopMaterialNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ShopMaterialNorms::getMaterialId), materialId);
        List<ShopMaterialNorms> shopMaterialNormsList = list(queryWrapper);
        Map<String, List<ShopMaterialNorms>> collect = shopMaterialNormsList.stream().collect(Collectors.groupingBy(ShopMaterialNorms::getMaterialId));
        return collect;
    }

    @Override
    public void saveList(String materialId, List<ShopMaterialNorms> shopMaterialNormsList) {
        deleteByMaterialId(materialId);
        if (CollectionUtil.isNotEmpty(shopMaterialNormsList)) {
            for (ShopMaterialNorms shopMaterialNorms : shopMaterialNormsList) {
                shopMaterialNorms.setMaterialId(materialId);
                shopMaterialNorms.setRealSales(CommonNumConstants.NUM_ZERO.toString());
            }
            createEntity(shopMaterialNormsList, StrUtil.EMPTY);
        }
    }

    @Override
    @IgnoreTenant
    public List<ShopMaterialNorms> queryShopMaterialByNormsIdList(List<String> normsIdList) {
        if (CollectionUtil.isEmpty(normsIdList)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<ShopMaterialNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ShopMaterialNorms::getNormsId), normsIdList);
        List<ShopMaterialNorms> shopMaterialNormsList = list(queryWrapper);
        return shopMaterialNormsList;
    }
}
