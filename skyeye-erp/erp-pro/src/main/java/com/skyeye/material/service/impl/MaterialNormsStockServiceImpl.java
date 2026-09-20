/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.dao.MaterialNormsStockDao;
import com.skyeye.material.entity.MaterialNormsStock;
import com.skyeye.material.service.MaterialNormsStockService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MaterialNormsStockServiceImpl
 * @Description: ERP商品规格初始化库存服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/21 17:48
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品规格库存", groupName = "商品管理", manageShow = false)
public class MaterialNormsStockServiceImpl extends SkyeyeBusinessServiceImpl<MaterialNormsStockDao, MaterialNormsStock> implements MaterialNormsStockService {

    @Autowired
    private MaterialNormsStockDao materialNormsStockDao;

    /**
     * 根据商品id删除对应的初始化库存信息
     *
     * @param materialId 商品id
     */
    @Override
    public void deleteNormsInitStockByMaterialId(String materialId) {
        QueryWrapper<MaterialNormsStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsStock::getMaterialId), materialId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsStock::getType), MaterialNormsStockType.INIT_STOCK.getKey());
        remove(queryWrapper);
    }

    /**
     * 根据规格id获取商品规格的当前库存信息
     *
     * @param normsIds 规格id集合
     * @param depotId  仓库id
     * @return
     */
    @Override
    public Map<String, String> queryMaterialNormsStock(List<String> normsIds, String depotId) {
        // 查询规格信息
        List<Map<String, Object>> normStockList = materialNormsStockDao.queryMaterialStockByNormsId(normsIds, depotId);
        if (CollectionUtil.isEmpty(normStockList)) {
            normStockList = new ArrayList<>();
        }
        Map<String, String> normStockMap = normStockList.stream()
            .collect(Collectors.toMap(item -> item.get("normsId").toString(),
                item -> item.get("stock") == null ? CommonNumConstants.NUM_ZERO.toString() : item.get("stock").toString()));
        normsIds.forEach(normsId -> {
            if (!normStockMap.containsKey(normsId)) {
                normStockMap.put(normsId, CommonNumConstants.NUM_ZERO.toString());
            }
        });
        return normStockMap;
    }

    /**
     * 批量获取指定类型的规格库存信息
     *
     * @param normsIds 规格id集合
     * @return
     */
    @Override
    @IgnoreTenant
    public Map<String, List<MaterialNormsStock>> queryNormsStockByNormsId(List<String> normsIds, Integer type) {
        if (CollectionUtil.isEmpty(normsIds)) {
            return new HashMap<>();
        }
        List<MaterialNormsStock> beans = materialNormsStockDao.queryNormsStockByNormsId(normsIds, StringUtils.EMPTY, type);
        Map<String, List<MaterialNormsStock>> initStockMap = beans.stream().collect(Collectors.groupingBy(MaterialNormsStock::getNormsId));
        normsIds.forEach(normsId -> {
            if (initStockMap.get(normsId) == null) {
                initStockMap.put(normsId, new ArrayList<>());
            }
        });
        return initStockMap;
    }

    /**
     * 保存由单据操作生成的库存信息
     *
     * @param materialId 商品id
     * @param depotId    仓库id
     * @param normsId    规格id
     * @param stock      库存数量
     */
    @Override
    public String saveMaterialNormsStock(String materialId, String depotId, String normsId, String stock, int stockType) {
        QueryWrapper<MaterialNormsStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsStock::getNormsId), normsId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsStock::getDepotId), depotId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsStock::getType), stockType);
        MaterialNormsStock normsStock = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(normsStock)) {
            normsStock = new MaterialNormsStock();
            normsStock.setMaterialId(materialId);
            normsStock.setNormsId(normsId);
            normsStock.setDepotId(depotId);
            normsStock.setStock(stock);
            normsStock.setType(stockType);
            save(normsStock);
            return normsStock.getStock();
        } else {
            UpdateWrapper<MaterialNormsStock> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsStock::getNormsId), normsId);
            updateWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsStock::getDepotId), depotId);
            updateWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsStock::getType), stockType);
            updateWrapper.set(MybatisPlusUtil.toColumns(MaterialNormsStock::getStock), stock);
            update(updateWrapper);
            return normsStock.getStock();
        }
    }

    /**
     * 保存初始化库存信息
     *
     * @param materialId
     * @param normsStock
     * @param userId
     */
    @Override
    public void saveMaterialNormsInitStock(String materialId, List<MaterialNormsStock> normsStock, String userId) {
        // 存储初始化库存数量
        deleteNormsInitStockByMaterialId(materialId);
        if (CollectionUtil.isNotEmpty(normsStock)) {
            createEntity(normsStock, userId);
        }
    }

}
