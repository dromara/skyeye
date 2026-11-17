/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.dao.DepotLevelValOccupancyDao;
import com.skyeye.depot.entity.DepotLevelValOccupancy;
import com.skyeye.depot.service.DepotLevelValOccupancyService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "仓库库位占用", groupName = "仓库级别管理")
public class DepotLevelValOccupancyServiceImpl
    extends SkyeyeBusinessServiceImpl<DepotLevelValOccupancyDao, DepotLevelValOccupancy>
    implements DepotLevelValOccupancyService {

    @Override
    public void queryDepotLevelValOccupancyByDepotId(InputObject inputObject, OutputObject outputObject) {
        String depotId = inputObject.getParams().get("depotId").toString();
        if (StrUtil.isBlank(depotId)) {
            throw new CustomException("仓库id不能为空");
        }
        QueryWrapper<DepotLevelValOccupancy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevelValOccupancy::getDepotId), depotId);
        List<DepotLevelValOccupancy> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void writeDepotLevelValOccupancyBatch(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String depotId = String.valueOf(params.get("depotId"));
        if (StrUtil.isBlank(depotId) || "null".equals(depotId)) {
            throw new CustomException("仓库id不能为空");
        }
        Object listObj = params.get("occupancyList");
        if (listObj == null) {
            throw new CustomException("占用数据不能为空");
        }
        JSONArray arr = listObj instanceof JSONArray
            ? (JSONArray) listObj
            : JSONUtil.parseArray(JSONUtil.toJsonStr(listObj));
        List<DepotLevelValOccupancy> incoming = JSONUtil.toList(arr, DepotLevelValOccupancy.class);

        QueryWrapper<DepotLevelValOccupancy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevelValOccupancy::getDepotId), depotId);
        List<DepotLevelValOccupancy> exists = list(queryWrapper);
        Map<String, DepotLevelValOccupancy> existMap = exists.stream()
            .collect(Collectors.toMap(DepotLevelValOccupancy::getLevelValId, e -> e, (a, b) -> a));

        if (CollectionUtil.isEmpty(incoming)) {
            if (CollectionUtil.isNotEmpty(exists)) {
                deleteById(exists.stream().map(DepotLevelValOccupancy::getId).collect(Collectors.toList()));
            }
            outputObject.setBeans(new ArrayList<>());
            outputObject.settotal(0);
            return;
        }

        List<DepotLevelValOccupancy> toCreate = new ArrayList<>();
        List<DepotLevelValOccupancy> toUpdate = new ArrayList<>();
        for (DepotLevelValOccupancy item : incoming) {
            if (StrUtil.isBlank(item.getLevelValId())) {
                throw new CustomException("库位值id不能为空");
            }
            item.setDepotId(depotId);
            if (StrUtil.isBlank(item.getQty())) {
                item.setQty("0");
            }
            if (StrUtil.isBlank(item.getCapacity())) {
                item.setCapacity("1");
            }
            if (item.getWarn() == null) {
                item.setWarn(0);
            }
            DepotLevelValOccupancy old = existMap.remove(item.getLevelValId());
            if (old == null) {
                item.setId(null);
                toCreate.add(item);
            } else {
                item.setId(old.getId());
                toUpdate.add(item);
            }
        }
        String userId = String.valueOf(inputObject.getLogParams().get("id"));
        if (CollectionUtil.isNotEmpty(toCreate)) {
            createEntity(toCreate, userId);
        }
        if (CollectionUtil.isNotEmpty(toUpdate)) {
            updateEntity(toUpdate, userId);
        }
        if (CollectionUtil.isNotEmpty(existMap)) {
            deleteById(existMap.values().stream().map(DepotLevelValOccupancy::getId).collect(Collectors.toList()));
        }
        queryDepotLevelValOccupancyByDepotId(inputObject, outputObject);
    }

    @Override
    public void deleteByLevelValIds(List<String> levelValIds) {
        if (CollectionUtil.isEmpty(levelValIds)) {
            return;
        }
        List<String> ids = levelValIds.stream()
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        QueryWrapper<DepotLevelValOccupancy> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DepotLevelValOccupancy::getLevelValId), ids);
        remove(queryWrapper);
    }

    @Override
    public void adjustOccupancyQty(String depotId, String levelValId, String operNumber, int putOrOutType, String userId) {
        if (StrUtil.isBlank(depotId) || StrUtil.isBlank(levelValId) || StrUtil.isBlank(operNumber)) {
            return;
        }
        QueryWrapper<DepotLevelValOccupancy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevelValOccupancy::getDepotId), depotId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevelValOccupancy::getLevelValId), levelValId);
        DepotLevelValOccupancy row = getOne(queryWrapper, false);

        String delta = operNumber;
        String cur = row != null && StrUtil.isNotBlank(row.getQty()) ? row.getQty() : CommonNumConstants.NUM_ZERO.toString();
        String nextQty;
        if (putOrOutType == DepotPutOutType.OUT.getKey()) {
            // 出库不低于 0
            if (CalculationUtil.compareTo(cur, delta, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                nextQty = CommonNumConstants.NUM_ZERO.toString();
            } else {
                nextQty = CalculationUtil.subtract(cur, delta, ErpConstants.NUM_AFTER_DOT);
            }
        } else {
            // 默认按入库累加
            nextQty = CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, cur, delta);
        }

        if (row == null) {
            DepotLevelValOccupancy create = new DepotLevelValOccupancy();
            create.setDepotId(depotId);
            create.setLevelValId(levelValId);
            create.setQty(nextQty);
            // 容量后续可在占用表维护；默认 100 避免首次入库即显示「近满」
            create.setCapacity("100");
            create.setWarn(0);
            createEntity(Collections.singletonList(create), StrUtil.blankToDefault(userId, StrUtil.EMPTY));
            return;
        }
        row.setQty(nextQty);
        updateEntity(Collections.singletonList(row), StrUtil.blankToDefault(userId, StrUtil.EMPTY));
    }
}
