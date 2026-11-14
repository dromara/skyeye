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
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.dao.DepotSpaceLayoutDao;
import com.skyeye.depot.entity.DepotSpaceLayout;
import com.skyeye.depot.service.DepotSpaceLayoutService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "仓库3D空间布局", groupName = "仓库级别管理")
public class DepotSpaceLayoutServiceImpl extends SkyeyeBusinessServiceImpl<DepotSpaceLayoutDao, DepotSpaceLayout>
    implements DepotSpaceLayoutService {

    @Override
    public void queryDepotSpaceLayoutByDepotId(InputObject inputObject, OutputObject outputObject) {
        String depotId = inputObject.getParams().get("depotId").toString();
        if (StrUtil.isBlank(depotId)) {
            throw new CustomException("仓库id不能为空");
        }
        QueryWrapper<DepotSpaceLayout> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotSpaceLayout::getDepotId), depotId);
        List<DepotSpaceLayout> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void writeDepotSpaceLayoutBatch(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String depotId = String.valueOf(params.get("depotId"));
        if (StrUtil.isBlank(depotId) || "null".equals(depotId)) {
            throw new CustomException("仓库id不能为空");
        }
        Object listObj = params.get("layoutList");
        if (listObj == null) {
            throw new CustomException("布局数据不能为空");
        }
        JSONArray arr = listObj instanceof JSONArray
            ? (JSONArray) listObj
            : JSONUtil.parseArray(JSONUtil.toJsonStr(listObj));
        List<DepotSpaceLayout> incoming = JSONUtil.toList(arr, DepotSpaceLayout.class);
        if (CollectionUtil.isEmpty(incoming)) {
            // 允许清空：删除该仓全部布局
            QueryWrapper<DepotSpaceLayout> del = new QueryWrapper<>();
            del.eq(MybatisPlusUtil.toColumns(DepotSpaceLayout::getDepotId), depotId);
            remove(del);
            outputObject.setBeans(new ArrayList<>());
            return;
        }

        QueryWrapper<DepotSpaceLayout> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotSpaceLayout::getDepotId), depotId);
        List<DepotSpaceLayout> exists = list(queryWrapper);
        Map<String, DepotSpaceLayout> existMap = exists.stream()
            .collect(Collectors.toMap(DepotSpaceLayout::getLevelValId, e -> e, (a, b) -> a));

        List<DepotSpaceLayout> toCreate = new ArrayList<>();
        List<DepotSpaceLayout> toUpdate = new ArrayList<>();
        for (DepotSpaceLayout item : incoming) {
            if (StrUtil.isBlank(item.getLevelValId())) {
                throw new CustomException("库位值id不能为空");
            }
            item.setDepotId(depotId);
            DepotSpaceLayout old = existMap.remove(item.getLevelValId());
            if (old == null) {
                item.setId(null);
                toCreate.add(item);
            } else {
                item.setId(old.getId());
                toUpdate.add(item);
            }
        }
        if (CollectionUtil.isNotEmpty(toCreate)) {
            createEntity(toCreate, String.valueOf(inputObject.getLogParams().get("id")));
        }
        if (CollectionUtil.isNotEmpty(toUpdate)) {
            updateEntity(toUpdate, String.valueOf(inputObject.getLogParams().get("id")));
        }
        // 未提交的旧布局删除
        if (CollectionUtil.isNotEmpty(existMap)) {
            deleteById(existMap.values().stream().map(DepotSpaceLayout::getId).collect(Collectors.toList()));
        }
        queryDepotSpaceLayoutByDepotId(inputObject, outputObject);
    }

    @Override
    public void deleteDepotSpaceLayoutByLevelValId(InputObject inputObject, OutputObject outputObject) {
        String levelValId = inputObject.getParams().get("levelValId").toString();
        if (StrUtil.isBlank(levelValId)) {
            throw new CustomException("库位值id不能为空");
        }
        QueryWrapper<DepotSpaceLayout> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotSpaceLayout::getLevelValId), levelValId);
        remove(queryWrapper);
    }
}
