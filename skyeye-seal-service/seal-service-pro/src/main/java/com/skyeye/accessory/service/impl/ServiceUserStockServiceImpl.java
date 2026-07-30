/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.accessory.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.accessory.classenum.UserStockPutOutType;
import com.skyeye.accessory.dao.ServiceUserStockDao;
import com.skyeye.accessory.entity.ServiceUserStock;
import com.skyeye.accessory.service.ServiceUserStockService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ServiceUserStockServiceImpl
 * @Description: 用户配件申领单审核通过后的库存信息服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/13 22:25
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户配件申领单审核通过后的库存信息", groupName = "用户配件申领单审核通过后的库存信息")
public class ServiceUserStockServiceImpl extends SkyeyeBusinessServiceImpl<ServiceUserStockDao, ServiceUserStock> implements ServiceUserStockService {

    private static Logger LOGGER = LoggerFactory.getLogger(ServiceUserStockServiceImpl.class);

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private IMaterialService iMaterialService;

    @Override
    public QueryWrapper<ServiceUserStock> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ServiceUserStock> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceUserStock::getUserId), userId);
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iMaterialService.setMationForMap(beans, "materialId", "materialMation");
        iMaterialNormsService.setMationForMap(beans, "normsId", "normsMation");
        return beans;
    }

    /**
     * 修改用户拥有的商品规格库存
     *
     * @param userId     仓库id
     * @param materialId 商品id
     * @param normsId    规格id
     * @param operNumber 变化数量
     * @param type       出入库类型，{@link com.skyeye.accessory.classenum.UserStockPutOutType}
     */
    @Override
    public void editMaterialNormsUserStock(String userId, String materialId, String normsId, String operNumber, int type) {
        String lockKey = String.format("userStock_%s_%s", userId, normsId);
        RedisLock lock = new RedisLock(lockKey);
        try {
            if (!lock.lock()) {
                // 加锁失败
                throw new CustomException("增减库存失败，当前并发量较大，请稍后再次尝试.");
            }
            LOGGER.info("get lock success, lockKey is {}.", lockKey);
            // 变化的数量
            ServiceUserStock serviceUserStock = queryUserStock(userId, normsId);
            // 如果该规格在指定仓库中已经有存储数据，则直接做修改
            if (ObjectUtil.isNotEmpty(serviceUserStock)) {
                String stockNumStr = String.valueOf(serviceUserStock.getStock());
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
        if (type == UserStockPutOutType.PUT.getKey()) {
            // 入库
            stockNum = CalculationUtil.add(stockNum, changeNumber, CommonNumConstants.NUM_TWO);
        } else if (type == UserStockPutOutType.OUT.getKey()) {
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

    /**
     * 根据配件规格id获取我的库存
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyPartsNumByNormsId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String userId = inputObject.getLogParams().get("id").toString();
        String normsId = map.get("normsId").toString();
        ServiceUserStock serviceUserStock = queryUserStock(userId, normsId);
        outputObject.setBean(serviceUserStock);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryUserStockByNormsIds(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String userId = inputObject.getLogParams().get("id").toString();
        List<String> normsIds = JSONUtil.toList(map.get("normsIds").toString(), null);
        Map<String, ServiceUserStock> stockMap = queryUserStock(userId, normsIds);
        outputObject.setBeans(new ArrayList<>(stockMap.values()));
        outputObject.settotal(stockMap.size());
    }

    @Override
    public void editMaterialNormsUserStockForFeign(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        if (map.get("userId") == null) {
            throw new CustomException("库存所属用户不能为空");
        }
        editMaterialNormsUserStock(
            map.get("userId").toString(),
            map.get("materialId").toString(),
            map.get("normsId").toString(),
            map.get("operNumber").toString(),
            Integer.parseInt(map.get("type").toString()));
    }

    @Override
    public ServiceUserStock queryUserStock(String userId, String normsId) {
        QueryWrapper<ServiceUserStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceUserStock::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceUserStock::getNormsId), normsId);
        return getOne(queryWrapper);
    }

    @Override
    public Map<String, ServiceUserStock> queryUserStock(String userId, List<String> normsIds) {
        QueryWrapper<ServiceUserStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceUserStock::getUserId), userId);
        queryWrapper.in(MybatisPlusUtil.toColumns(ServiceUserStock::getNormsId), normsIds);
        List<ServiceUserStock> userStocks = list(queryWrapper);
        if (CollectionUtil.isEmpty(userStocks)) {
            return MapUtil.newHashMap();
        }
        return userStocks.stream().collect(Collectors.toMap(ServiceUserStock::getNormsId, bean -> bean));
    }

    @Override
    public void updateStock(String userId, String normsId, String stock) {
        UpdateWrapper<ServiceUserStock> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ServiceUserStock::getUserId), userId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ServiceUserStock::getNormsId), normsId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ServiceUserStock::getStock), stock);
        update(updateWrapper);
    }

    private void saveStock(String userId, String materialId, String normsId, String stock) {
        ServiceUserStock serviceUserStock = new ServiceUserStock();
        serviceUserStock.setUserId(userId);
        serviceUserStock.setMaterialId(materialId);
        serviceUserStock.setNormsId(normsId);
        serviceUserStock.setStock(stock);
        createEntity(serviceUserStock, userId);
    }

}
