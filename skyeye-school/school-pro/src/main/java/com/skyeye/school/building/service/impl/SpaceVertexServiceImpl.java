package com.skyeye.school.building.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.building.dao.SpaceVertexDao;
import com.skyeye.school.building.entity.SpaceVertex;
import com.skyeye.school.building.floorenum.SpaceOwnerTypeEnum;
import com.skyeye.school.building.service.SpaceVertexService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 空间轮廓顶点
 */
@Service
@SkyeyeService(name = "空间轮廓", groupName = "地点管理")
public class SpaceVertexServiceImpl extends SkyeyeBusinessServiceImpl<SpaceVertexDao, SpaceVertex> implements SpaceVertexService {

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void writeSpaceOutline(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Integer ownerType = Integer.valueOf(params.get("ownerType").toString());
        String ownerId = params.get("ownerId").toString();
        if (!SpaceOwnerTypeEnum.BUILDING.getKey().equals(ownerType) && !SpaceOwnerTypeEnum.ROOM.getKey().equals(ownerType)) {
            throw new CustomException("轮廓归属类型不正确");
        }
        if (StrUtil.isEmpty(ownerId)) {
            throw new CustomException("轮廓归属不能为空");
        }
        Object vertexRaw = params.get("vertexList");
        List<SpaceVertex> vertices = new ArrayList<>();
        if (vertexRaw != null && StrUtil.isNotEmpty(vertexRaw.toString())) {
            vertices = JSONUtil.toList(vertexRaw.toString(), SpaceVertex.class);
        }
        if (CollectionUtil.isNotEmpty(vertices) && vertices.size() < 3) {
            throw new CustomException("范围至少需要3个点");
        }
        deleteByOwners(ownerType, Collections.singletonList(ownerId));
        if (CollectionUtil.isEmpty(vertices)) {
            return;
        }
        int orderNum = 1;
        for (SpaceVertex vertex : vertices) {
            vertex.setId(null);
            vertex.setOwnerType(ownerType);
            vertex.setOwnerId(ownerId);
            vertex.setOrderNum(orderNum++);
            if (vertex.getLongitude() != null) {
                vertex.setLongitude(vertex.getLongitude().trim());
            }
            if (vertex.getLatitude() != null) {
                vertex.setLatitude(vertex.getLatitude().trim());
            }
            if (StrUtil.isEmpty(vertex.getLongitude()) || StrUtil.isEmpty(vertex.getLatitude())) {
                throw new CustomException("轮廓顶点经纬度不能为空");
            }
        }
        String userId = inputObject.getLogParams().get("id").toString();
        createEntity(vertices, userId);
    }

    @Override
    public void querySpaceVertexByOwner(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Integer ownerType = Integer.valueOf(params.get("ownerType").toString());
        String ownerId = params.get("ownerId").toString();
        List<SpaceVertex> vertices = queryGroupByOwner(ownerType, Collections.singletonList(ownerId))
            .getOrDefault(ownerId, new ArrayList<>());
        outputObject.setBeans(vertices);
        outputObject.settotal(vertices.size());
    }

    @Override
    public void deleteByOwners(Integer ownerType, List<String> ownerIds) {
        if (ownerType == null || CollectionUtil.isEmpty(ownerIds)) {
            return;
        }
        QueryWrapper<SpaceVertex> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SpaceVertex::getOwnerType), ownerType);
        queryWrapper.in(MybatisPlusUtil.toColumns(SpaceVertex::getOwnerId), ownerIds);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<SpaceVertex>> queryGroupByOwner(Integer ownerType, List<String> ownerIds) {
        Map<String, List<SpaceVertex>> grouped = new LinkedHashMap<>();
        if (ownerType == null || CollectionUtil.isEmpty(ownerIds)) {
            return grouped;
        }
        QueryWrapper<SpaceVertex> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SpaceVertex::getOwnerType), ownerType);
        queryWrapper.in(MybatisPlusUtil.toColumns(SpaceVertex::getOwnerId), ownerIds);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(SpaceVertex::getOrderNum));
        List<SpaceVertex> vertices = list(queryWrapper);
        for (SpaceVertex vertex : vertices) {
            grouped.computeIfAbsent(vertex.getOwnerId(), key -> new ArrayList<>()).add(vertex);
        }
        return grouped;
    }
}
