/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.building.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exception.CustomException;
import com.skyeye.school.building.dao.TeachBuildingDao;
import com.skyeye.school.building.entity.FloorInfo;
import com.skyeye.school.building.entity.RoomOccupant;
import com.skyeye.school.building.entity.SpaceVertex;
import com.skyeye.school.building.entity.TeachBuilding;
import com.skyeye.school.building.floorenum.FloorInfoEnum;
import com.skyeye.school.building.floorenum.SpaceOwnerTypeEnum;
import com.skyeye.school.building.service.FloorInfoService;
import com.skyeye.school.building.service.RoomOccupantService;
import com.skyeye.school.building.service.SpaceVertexService;
import com.skyeye.school.building.service.TeachBuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TeachBuildingServiceImpl
 * @Description: 地点管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/8/7 20:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "地点管理", groupName = "地点管理")
public class TeachBuildingServiceImpl extends SkyeyeBusinessServiceImpl<TeachBuildingDao, TeachBuilding> implements TeachBuildingService {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private FloorInfoService floorInfoService;

    @Autowired
    private SpaceVertexService spaceVertexService;

    @Autowired
    private RoomOccupantService roomOccupantService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public void selectById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        TeachBuilding building = selectById(id);
        iAuthUserService.setName(building, "createId", "createName");
        iAuthUserService.setName(building, "lastUpdateId", "lastUpdateName");
        outputObject.setBean(building);
    }

    @Override
    protected void updatePrepose(TeachBuilding entity) {
        TeachBuilding oldBuilding = selectById(entity.getId());
        entity.setLongitude(oldBuilding.getLongitude());
        entity.setLatitude(oldBuilding.getLatitude());
    }

    @Override
    protected void writePostpose(TeachBuilding entity, String userId) {
        super.writePostpose(entity, userId);
        // 删除缓存
        jedisClientService.del(getCacheKey(entity.getSchoolId()));
    }

    @Override
    protected void deletePostpose(TeachBuilding entity) {
        // 删除缓存
        jedisClientService.del(getCacheKey(entity.getSchoolId()));
    }

    @Override
    public void queryTeachBuildingBySchoolId(InputObject inputObject, OutputObject outputObject) {
        String schoolId = inputObject.getParams().get("schoolId").toString();
        if (StrUtil.isEmpty(schoolId)) {
            return;
        }
        String cacheKey = getCacheKey(schoolId);
        List<TeachBuilding> teachBuildingList = redisCache.getList(cacheKey, key -> {
            QueryWrapper<TeachBuilding> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(TeachBuilding::getSchoolId), schoolId);
            return list(queryWrapper);
        }, RedisConstants.A_YEAR_SECONDS, TeachBuilding.class);
        schoolService.setDataMation(teachBuildingList, TeachBuilding::getSchoolId);
        iAuthUserService.setName(teachBuildingList, "createId", "createName");
        iAuthUserService.setName(teachBuildingList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(teachBuildingList);
        outputObject.settotal(teachBuildingList.size());
    }

    private String getCacheKey(String schoolId) {
        return String.format(Locale.ROOT, "school:teachBuilding:all:%s", schoolId);
    }

    @Override
    public void queryTeachBuildingByHolderId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String schoolId = params.get("schoolId").toString();
        String typeId = params.get("typeId").toString();
        QueryWrapper<TeachBuilding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TeachBuilding::getSchoolId), schoolId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TeachBuilding::getTypeId), typeId);
        List<TeachBuilding> teachBuildingList = list(queryWrapper);
        outputObject.setBeans(teachBuildingList);
        outputObject.settotal(teachBuildingList.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getLocationId), id);
        List<FloorInfo> nodes = floorInfoService.list(queryWrapper);
        List<String> roomIds = nodes.stream()
            .filter(item -> FloorInfoEnum.ClASS_INFO_ENUM.getKey().equals(item.getNodeType()))
            .map(FloorInfo::getId)
            .collect(Collectors.toList());
        spaceVertexService.deleteByOwners(SpaceOwnerTypeEnum.BUILDING.getKey(), Collections.singletonList(id));
        spaceVertexService.deleteByOwners(SpaceOwnerTypeEnum.ROOM.getKey(), roomIds);
        roomOccupantService.deleteByFloorInfoIds(roomIds);
        deleteById(id);
        floorInfoService.remove(queryWrapper);
    }

    @Override
    public void queryCampusTwinBySchoolId(InputObject inputObject, OutputObject outputObject) {
        String schoolId = inputObject.getParams().get("schoolId").toString();
        if (StrUtil.isEmpty(schoolId)) {
            return;
        }
        QueryWrapper<TeachBuilding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TeachBuilding::getSchoolId), schoolId);
        List<TeachBuilding> buildings = list(queryWrapper);
        List<Map<String, Object>> rows = buildTwinRows(buildings);
        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }

    @Override
    public void queryBuildingTwinByLocationId(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        TeachBuilding building = selectById(id);
        if (ObjectUtil.isEmpty(building) || StrUtil.isEmpty(building.getId())) {
            throw new CustomException("该地点不存在。");
        }
        List<Map<String, Object>> rows = buildTwinRows(Collections.singletonList(building));
        outputObject.setBean(rows.isEmpty() ? new LinkedHashMap<>() : rows.get(0));
    }

    private List<Map<String, Object>> buildTwinRows(List<TeachBuilding> buildings) {
        if (CollectionUtil.isEmpty(buildings)) {
            return new ArrayList<>();
        }
        List<String> buildingIds = buildings.stream().map(TeachBuilding::getId).collect(Collectors.toList());
        Map<String, List<SpaceVertex>> buildingOutlines = spaceVertexService.queryGroupByOwner(
            SpaceOwnerTypeEnum.BUILDING.getKey(), buildingIds);

        QueryWrapper<FloorInfo> floorQuery = new QueryWrapper<>();
        floorQuery.in(MybatisPlusUtil.toColumns(FloorInfo::getLocationId), buildingIds);
        List<FloorInfo> nodes = floorInfoService.list(floorQuery);
        Map<String, List<FloorInfo>> nodesByLocation = nodes.stream()
            .collect(Collectors.groupingBy(FloorInfo::getLocationId));

        List<String> roomIds = nodes.stream()
            .filter(item -> FloorInfoEnum.ClASS_INFO_ENUM.getKey().equals(item.getNodeType()))
            .map(FloorInfo::getId)
            .collect(Collectors.toList());
        Map<String, List<SpaceVertex>> roomOutlines = spaceVertexService.queryGroupByOwner(
            SpaceOwnerTypeEnum.ROOM.getKey(), roomIds);
        Map<String, List<RoomOccupant>> occupants = roomOccupantService.queryGroupByFloorInfoIds(roomIds);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (TeachBuilding building : buildings) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", building.getId());
            row.put("name", building.getName());
            row.put("logo", building.getLogo());
            row.put("longitude", building.getLongitude());
            row.put("latitude", building.getLatitude());
            row.put("schoolId", building.getSchoolId());
            row.put("outline", toVertexMaps(buildingOutlines.get(building.getId())));
            row.put("floors", buildFloors(nodesByLocation.get(building.getId()), roomOutlines, occupants));
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildFloors(List<FloorInfo> nodes,
                                                  Map<String, List<SpaceVertex>> roomOutlines,
                                                  Map<String, List<RoomOccupant>> occupants) {
        if (CollectionUtil.isEmpty(nodes)) {
            return new ArrayList<>();
        }
        List<FloorInfo> floors = nodes.stream()
            .filter(item -> FloorInfoEnum.FLOOR_INFO_ENUM.getKey().equals(item.getNodeType()))
            .sorted(Comparator
                .comparing((FloorInfo item) -> item.getFloorIndex() == null ? Integer.MAX_VALUE : item.getFloorIndex())
                .thenComparing(item -> item.getSortOrder() == null ? 0 : item.getSortOrder()))
            .collect(Collectors.toList());
        Map<String, List<FloorInfo>> roomsByParent = nodes.stream()
            .filter(item -> FloorInfoEnum.ClASS_INFO_ENUM.getKey().equals(item.getNodeType()))
            .collect(Collectors.groupingBy(FloorInfo::getParentId));

        List<Map<String, Object>> floorRows = new ArrayList<>();
        for (FloorInfo floor : floors) {
            Map<String, Object> floorRow = new LinkedHashMap<>();
            floorRow.put("id", floor.getId());
            floorRow.put("name", floor.getName());
            floorRow.put("floorIndex", floor.getFloorIndex());
            floorRow.put("sortOrder", floor.getSortOrder());
            floorRow.put("remark", floor.getRemark());
            floorRow.put("status", floor.getStatus());
            List<FloorInfo> rooms = roomsByParent.getOrDefault(floor.getId(), new ArrayList<>());
            rooms.sort(Comparator.comparing(item -> item.getSortOrder() == null ? 0 : item.getSortOrder()));
            List<Map<String, Object>> roomRows = new ArrayList<>();
            for (FloorInfo room : rooms) {
                Map<String, Object> roomRow = new LinkedHashMap<>();
                roomRow.put("id", room.getId());
                roomRow.put("name", room.getName());
                roomRow.put("remark", room.getRemark());
                roomRow.put("sortOrder", room.getSortOrder());
                roomRow.put("status", room.getStatus());
                roomRow.put("modelColor", room.getModelColor());
                roomRow.put("logo", room.getLogo());
                roomRow.put("outline", toVertexMaps(roomOutlines.get(room.getId())));
                roomRow.put("occupants", toOccupantMaps(occupants.get(room.getId())));
                roomRows.add(roomRow);
            }
            floorRow.put("rooms", roomRows);
            floorRows.add(floorRow);
        }
        return floorRows;
    }

    private List<Map<String, Object>> toVertexMaps(List<SpaceVertex> vertices) {
        if (CollectionUtil.isEmpty(vertices)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SpaceVertex vertex : vertices) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("longitude", vertex.getLongitude());
            row.put("latitude", vertex.getLatitude());
            row.put("orderNum", vertex.getOrderNum());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> toOccupantMaps(List<RoomOccupant> occupants) {
        if (CollectionUtil.isEmpty(occupants)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        for (RoomOccupant occupant : occupants) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", occupant.getId());
            row.put("personType", occupant.getPersonType());
            row.put("personId", occupant.getPersonId());
            row.put("personName", occupant.getPersonName());
            row.put("duty", occupant.getDuty());
            row.put("remark", occupant.getRemark());
            rows.add(row);
        }
        return rows;
    }

    @Override
    public void editTeachBuildingLocationById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String longitude = params.get("longitude").toString();
        String latitude = params.get("latitude").toString();
        TeachBuilding building = selectById(id);
        if (ObjectUtil.isEmpty(building) || StrUtil.isEmpty(building.getId())) {
            throw new CustomException("该地点不存在。");
        }
        // 更新地点坐标
        UpdateWrapper<TeachBuilding> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(TeachBuilding::getLongitude), longitude);
        updateWrapper.set(MybatisPlusUtil.toColumns(TeachBuilding::getLatitude), latitude);
        update(updateWrapper);
        // 更新缓存
        refreshCache(id);

        // 更新指定学校下拥有的地点的缓存
        String cacheKey = getCacheKey(building.getSchoolId());
        jedisClientService.del(cacheKey);
    }

}
