/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.sparepart.classenum.EquipmentUserStockPutOutType;
import com.skyeye.sparepart.dao.EquipmentUserStockDao;
import com.skyeye.sparepart.entity.EquipmentUserStock;
import com.skyeye.sparepart.service.EquipmentUserStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备维修-我的备件库存
 */
@Service
@SkyeyeService(name = "我的备件库存", groupName = "设备备件")
public class EquipmentUserStockServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentUserStockDao, EquipmentUserStock>
    implements EquipmentUserStockService {

    private static Logger LOGGER = LoggerFactory.getLogger(EquipmentUserStockServiceImpl.class);

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Override
    public QueryWrapper<EquipmentUserStock> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentUserStock> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentUserStock::getUserId), userId);
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        materialService.setMationForMap(beans, "materialId", "materialMation");
        materialNormsService.setMationForMap(beans, "normsId", "normsMation");
        return beans;
    }

    /**
     * 修改用户拥有的商品规格库存
     *
     * @param userId     用户id
     * @param materialId 商品id
     * @param normsId    规格id
     * @param operNumber 变化数量
     * @param type       参考#EquipmentUserStockPutOutType枚举类
     */
    @Override
    public void editMaterialNormsUserStock(String userId, String materialId, String normsId, String operNumber, int type) {
        String lockKey = String.format("equipmentUserStock_%s_%s", userId, normsId);
        RedisLock lock = new RedisLock(lockKey);
        try {
            if (!lock.lock()) {
                // 加锁失败
                throw new CustomException("增减库存失败，当前并发量较大，请稍后再次尝试.");
            }
            LOGGER.info("get lock success, lockKey is {}.", lockKey);
            EquipmentUserStock userStock = queryUserStock(userId, normsId);
            if (ObjectUtil.isNotEmpty(userStock)) {
                String stockNumStr = String.valueOf(userStock.getStock());
                LOGGER.info("update user stock normsId【{}】 Stock. type is {}, old stockNum is {}, change stockNum is {}", normsId, type, stockNumStr, operNumber);
                stockNumStr = getNewStockNum(type, operNumber, stockNumStr);
                updateStock(userId, normsId, stockNumStr);
            } else {
                String stockNumStr = CommonNumConstants.NUM_ZERO.toString();
                LOGGER.info("insert user stock normsId【{}】 Stock. type is {}, change stockNum is {}", normsId, type, operNumber);
                stockNumStr = getNewStockNum(type, operNumber, stockNumStr);
                saveStock(userId, materialId, normsId, stockNumStr);
            }
            LOGGER.info("editMaterialNormsUserStock is success.");
        } catch (Exception ee) {
            LOGGER.warn("editMaterialNormsUserStock error, because {}", ee);
            if (ee instanceof CustomException) {
                throw new CustomException(ee.getMessage());
            }
            throw new RuntimeException(ee.getMessage());
        } finally {
            lock.unlock();
        }
    }

    private String getNewStockNum(int type, String changeNumber, String stockNum) {
        if (type == EquipmentUserStockPutOutType.PUT.getKey()) {
            // 入库
            stockNum = CalculationUtil.add(stockNum, changeNumber, CommonNumConstants.NUM_TWO);
        } else if (type == EquipmentUserStockPutOutType.OUT.getKey()) {
            // 出库
            stockNum = CalculationUtil.subtract(stockNum, changeNumber, CommonNumConstants.NUM_TWO);
        } else {
            throw new CustomException("状态错误");
        }
        if (CalculationUtil.compareTo(stockNum, CommonNumConstants.NUM_ZERO.toString(), 0, RoundingMode.UP) < 0) {
            throw new CustomException("库存不足，无法操作.");
        }
        return stockNum;
    }

    @Override
    public void queryMyPartsNumByNormsId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String userId = inputObject.getLogParams().get("id").toString();
        String normsId = map.get("normsId").toString();
        EquipmentUserStock userStock = queryUserStock(userId, normsId);
        outputObject.setBean(userStock);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public EquipmentUserStock queryUserStock(String userId, String normsId) {
        QueryWrapper<EquipmentUserStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentUserStock::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentUserStock::getNormsId), normsId);
        return getOne(queryWrapper);
    }

    @Override
    public Map<String, EquipmentUserStock> queryUserStock(String userId, List<String> normsIds) {
        QueryWrapper<EquipmentUserStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentUserStock::getUserId), userId);
        queryWrapper.in(MybatisPlusUtil.toColumns(EquipmentUserStock::getNormsId), normsIds);
        List<EquipmentUserStock> userStocks = list(queryWrapper);
        if (CollectionUtil.isEmpty(userStocks)) {
            return MapUtil.newHashMap();
        }
        return userStocks.stream().collect(Collectors.toMap(EquipmentUserStock::getNormsId, bean -> bean));
    }

    private void updateStock(String userId, String normsId, String stock) {
        UpdateWrapper<EquipmentUserStock> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(EquipmentUserStock::getUserId), userId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(EquipmentUserStock::getNormsId), normsId);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentUserStock::getStock), stock);
        update(updateWrapper);
    }

    private void saveStock(String userId, String materialId, String normsId, String stock) {
        EquipmentUserStock userStock = new EquipmentUserStock();
        userStock.setUserId(userId);
        userStock.setMaterialId(materialId);
        userStock.setNormsId(normsId);
        userStock.setStock(stock);
        createEntity(userStock, userId);
    }

}
