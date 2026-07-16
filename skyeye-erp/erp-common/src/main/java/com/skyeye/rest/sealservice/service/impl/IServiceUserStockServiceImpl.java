/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.sealservice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.sealservice.rest.IServiceUserStockRest;
import com.skyeye.rest.sealservice.service.IServiceUserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 我的配件库存公共服务实现
 */
@Service
public class IServiceUserStockServiceImpl implements IServiceUserStockService {

    @Autowired
    private IServiceUserStockRest iServiceUserStockRest;

    @Override
    public Map<String, Map<String, Object>> queryUserStock(List<String> normsIds) {
        if (CollectionUtil.isEmpty(normsIds)) {
            return MapUtil.newHashMap();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("normsIds", JSONUtil.toJsonStr(normsIds));
        List<Map<String, Object>> rows = ExecuteFeignClient.get(() -> iServiceUserStockRest.queryUserStockByNormsIds(params)).getRows();
        if (CollectionUtil.isEmpty(rows)) {
            return MapUtil.newHashMap();
        }
        return rows.stream()
            .filter(row -> row.get("normsId") != null)
            .collect(Collectors.toMap(row -> row.get("normsId").toString(), row -> row, (a, b) -> a));
    }

    @Override
    public void editMaterialNormsUserStock(String userId, String materialId, String normsId, String operNumber, int type) {
        if (userId == null) {
            throw new CustomException("库存所属用户不能为空");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("materialId", materialId);
        params.put("normsId", normsId);
        params.put("operNumber", operNumber);
        params.put("type", type);
        ExecuteFeignClient.get(() -> iServiceUserStockRest.editMaterialNormsUserStock(params));
    }

}
