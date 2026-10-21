package com.skyeye.school.building.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.building.dao.FloorInfoDao;
import com.skyeye.school.building.entity.FloorInfo;
import com.skyeye.school.building.entity.SpaceVertex;
import com.skyeye.school.building.entity.TeachBuilding;
import com.skyeye.school.building.floorenum.FloorInfoEnum;
import com.skyeye.school.building.floorenum.SpaceKindEnum;
import com.skyeye.school.building.floorenum.SpaceOwnerTypeEnum;
import com.skyeye.school.building.service.FloorInfoService;
import com.skyeye.school.building.service.RoomOccupantService;
import com.skyeye.school.building.service.SpaceVertexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: FloorInfoServiceImpl
 * @Description: 楼层教室服务管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "楼层教室服务管理", groupName = "楼层教室服务管理")
public class FloorInfoServiceImpl extends SkyeyeBusinessServiceImpl<FloorInfoDao, FloorInfo> implements FloorInfoService {

    @Autowired
    private SpaceVertexService spaceVertexService;

    @Autowired
    private RoomOccupantService roomOccupantService;

    @Override
    public void createPrepose(FloorInfo floorInfo) {
        Integer nodeType = floorInfo.getNodeType();
        String parentId = floorInfo.getParentId();
        if (nodeType == FloorInfoEnum.ClASS_INFO_ENUM.getKey()) {
            // 新增的节点是教室
            setDisabledStatus(floorInfo, parentId, FloorInfoEnum.ClASS_INFO_ENUM.getKey());
            if (floorInfo.getSpaceKind() == null) {
                floorInfo.setSpaceKind(SpaceKindEnum.ROOM.getKey());
            }
        } else if (nodeType == FloorInfoEnum.SERVICE_INFO_ENUM.getKey()) {
            // 新增的节点是服务
            setDisabledStatus(floorInfo, parentId, FloorInfoEnum.SERVICE_INFO_ENUM.getKey());
        } else {
            // 新增的节点是楼层
            floorInfo.setLevel(FloorInfoEnum.FLOOR_INFO_ENUM.getKey());
            floorInfo.setSpaceKind(null);
            floorInfo.setLinkGroupId(null);
        }
    }

    private void setDisabledStatus(FloorInfo floorInfo, String parentId, Integer level) {
        QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, parentId);
        FloorInfo one = getOne(queryWrapper);
        if (one.getStatus() == EnableEnum.DISABLE_USING.getKey()) {
            floorInfo.setStatus(EnableEnum.DISABLE_USING.getKey());
        }
        floorInfo.setLevel(level);
    }

    @Override
    public void updatePrepose(FloorInfo entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Integer nodeType = entity.getNodeType();
        String floorInfoId = entity.getId();
        Integer status = entity.getStatus();
        if (status == EnableEnum.DISABLE_USING.getKey()
            && nodeType != FloorInfoEnum.SERVICE_INFO_ENUM.getKey()) {
            if (nodeType == FloorInfoEnum.ClASS_INFO_ENUM.getKey()) {
                // 房间——同时禁用服务
                disabledServiceInfo(entity, userId);
            } else if (nodeType == FloorInfoEnum.FLOOR_INFO_ENUM.getKey()) {
                // 楼层——同时禁用房间和服务
                QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), floorInfoId);
                List<FloorInfo> bean = list(queryWrapper);
                if (CollectionUtil.isNotEmpty(bean)) {
                    for (FloorInfo floorInfo : bean) {
                        floorInfo.setStatus(EnableEnum.DISABLE_USING.getKey());
                        // 更新服务
                        disabledServiceInfo(floorInfo, userId);
                    }
                    updateEntity(bean, userId);
                }
            }
        }
    }

    private void disabledServiceInfo(FloorInfo entity, String userId) {
        QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), entity.getId());
        List<FloorInfo> bean = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(bean)) {
            for (FloorInfo floorInfo : bean) {
                floorInfo.setStatus(EnableEnum.DISABLE_USING.getKey());
            }
            updateEntity(bean, userId);
        }
    }

    @Override
    public void validatorEntity(FloorInfo floorInfo) {
        super.validatorEntity(floorInfo);
        Integer nodeType = floorInfo.getNodeType();
        String name = floorInfo.getName();
        QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getLocationId), floorInfo.getLocationId());
        if (nodeType == CommonNumConstants.NUM_ONE) {
            // 新增、更新楼层
            queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getName), name);
            long count = count(queryWrapper);
            if (StrUtil.isEmpty(floorInfo.getId()) && count > 0) {
                throw new CustomException("楼层名称已存在");
            }
            if (StrUtil.isNotEmpty(floorInfo.getId())) {
                FloorInfo floor = selectById(floorInfo.getId());
                if (!name.equals(floor.getName()) && count > 0) {
                    throw new CustomException("楼层名称已存在");
                }
            }
        } else if (nodeType == CommonNumConstants.NUM_TWO) {
            // 新增、更新教室
            queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), floorInfo.getParentId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getName), name);
            if (StrUtil.isEmpty(floorInfo.getId()) && count(queryWrapper) > 0) {
                throw new CustomException("教室名已存在");
            }
            if (StrUtil.isNotEmpty(floorInfo.getId())) {
                FloorInfo floor = selectById(floorInfo.getId());
                if (!name.equals(floor.getName()) && count(queryWrapper) > 0) {
                    throw new CustomException("教室名已存在");
                }
            }
        }
    }

    @Override
    public void deleteById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        FloorInfo floorInfo = selectById(id);
        Integer nodeType = floorInfo.getNodeType();
        if (nodeType == CommonNumConstants.NUM_TWO) {
            spaceVertexService.deleteByOwners(SpaceOwnerTypeEnum.ROOM.getKey(), Collections.singletonList(id));
            roomOccupantService.deleteByFloorInfoIds(Collections.singletonList(id));
        } else if (nodeType == CommonNumConstants.NUM_ONE) {
            QueryWrapper<FloorInfo> roomQuery = new QueryWrapper<>();
            roomQuery.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), id);
            List<String> roomIds = list(roomQuery).stream().map(FloorInfo::getId).collect(Collectors.toList());
            spaceVertexService.deleteByOwners(SpaceOwnerTypeEnum.ROOM.getKey(), roomIds);
            spaceVertexService.deleteByOwners(SpaceOwnerTypeEnum.FLOOR_PATH.getKey(), Collections.singletonList(id));
            roomOccupantService.deleteByFloorInfoIds(roomIds);
        }
        // 删除服务--直接删除
        super.deleteById(id);
        if (nodeType == CommonNumConstants.NUM_TWO) {
            // 删除教室--判断是否有服务，有则级联删除
            QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), id);
            remove(queryWrapper);
        } else if (nodeType == CommonNumConstants.NUM_ONE) {
            // 删除楼层--判断是否有教室，有则级联删除,删除服务
            QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), id);
            List<FloorInfo> classroomList = list(queryWrapper);
            remove(queryWrapper);
            for (FloorInfo classroom : classroomList) {
                // 删除服务
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), classroom.getId());
                remove(queryWrapper);
            }
        }
    }

    @Override
    public void queryFloorInfosByLocationId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String locationId = commonPageInfo.getHolderId();
        if (StrUtil.isEmpty(locationId)) {
            return;
        }
        String keyword = commonPageInfo.getKeyword();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<FloorInfo> queryWrapper = getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getLocationId), locationId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(FloorInfo::getSortOrder));
        List<FloorInfo> list = list(queryWrapper);
        iAuthUserService.setName(list, "createId", "createName");
        iAuthUserService.setName(list, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryFloorClassList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String keyword = (String) params.get("keyword");
        String schoolId = params.get("schoolId").toString();
        MPJLambdaWrapper<FloorInfo> queryWrapper = JoinWrappers.lambda("flw", FloorInfo.class)
            .innerJoin(TeachBuilding.class, "tb", TeachBuilding::getId, FloorInfo::getLocationId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TeachBuilding::getSchoolId), schoolId);
        if (StrUtil.isNotEmpty(keyword)) {
            queryWrapper.like("flw." + MybatisPlusUtil.toColumns(FloorInfo::getName), keyword);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getNodeType), FloorInfoEnum.ClASS_INFO_ENUM.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getStatus), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(FloorInfo::getCreateTime));
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            queryWrapper.eq("flw." + CommonConstants.TENANT_ID_FIELD, tenantId);
            queryWrapper.eq("tb." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        List<FloorInfo> bean = skyeyeBaseMapper.selectJoinList(FloorInfo.class, queryWrapper);
        outputObject.setBeans(bean);
        outputObject.settotal(bean.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void syncVerticalSpaceToFloors(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        FloorInfo source = selectById(id);
        if (source == null || !FloorInfoEnum.ClASS_INFO_ENUM.getKey().equals(source.getNodeType())) {
            throw new CustomException("请选择楼梯或电梯");
        }
        Integer kind = source.getSpaceKind() == null ? SpaceKindEnum.ROOM.getKey() : source.getSpaceKind();
        if (!SpaceKindEnum.STAIR.getKey().equals(kind) && !SpaceKindEnum.ELEVATOR.getKey().equals(kind)) {
            throw new CustomException("仅楼梯/电梯支持同步到其他楼层");
        }
        List<SpaceVertex> outline = spaceVertexService
            .queryGroupByOwner(SpaceOwnerTypeEnum.ROOM.getKey(), Collections.singletonList(id))
            .getOrDefault(id, new ArrayList<>());
        if (outline.size() < 3) {
            throw new CustomException("请先绘制楼梯/电梯范围再同步");
        }
        String userId = inputObject.getLogParams().get("id").toString();
        String groupId = StrUtil.isNotEmpty(source.getLinkGroupId()) ? source.getLinkGroupId() : source.getId();
        if (StrUtil.isEmpty(source.getLinkGroupId())) {
            source.setLinkGroupId(groupId);
            updateEntity(source, userId);
        }

        QueryWrapper<FloorInfo> floorQuery = new QueryWrapper<>();
        floorQuery.eq(MybatisPlusUtil.toColumns(FloorInfo::getLocationId), source.getLocationId());
        floorQuery.eq(MybatisPlusUtil.toColumns(FloorInfo::getNodeType), FloorInfoEnum.FLOOR_INFO_ENUM.getKey());
        List<FloorInfo> floors = list(floorQuery);
        int synced = 0;
        for (FloorInfo floor : floors) {
            if (StrUtil.equals(floor.getId(), source.getParentId())) {
                continue;
            }
            QueryWrapper<FloorInfo> existQuery = new QueryWrapper<>();
            existQuery.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), floor.getId());
            existQuery.eq(MybatisPlusUtil.toColumns(FloorInfo::getLinkGroupId), groupId);
            existQuery.eq(MybatisPlusUtil.toColumns(FloorInfo::getNodeType), FloorInfoEnum.ClASS_INFO_ENUM.getKey());
            FloorInfo existing = getOne(existQuery, false);
            String targetId;
            if (existing == null) {
                FloorInfo neu = new FloorInfo();
                neu.setLocationId(source.getLocationId());
                neu.setParentId(floor.getId());
                neu.setName(source.getName());
                neu.setNodeType(FloorInfoEnum.ClASS_INFO_ENUM.getKey());
                neu.setSpaceKind(kind);
                neu.setLinkGroupId(groupId);
                neu.setSpaceDir(source.getSpaceDir() == null ? 0 : source.getSpaceDir());
                neu.setModelColor(source.getModelColor());
                neu.setLogo(source.getLogo());
                neu.setStatus(source.getStatus());
                neu.setSortOrder(source.getSortOrder());
                neu.setRemark(source.getRemark());
                targetId = createEntity(neu, userId);
            } else {
                existing.setName(source.getName());
                existing.setSpaceKind(kind);
                existing.setLinkGroupId(groupId);
                existing.setSpaceDir(source.getSpaceDir() == null ? 0 : source.getSpaceDir());
                existing.setModelColor(source.getModelColor());
                existing.setLogo(source.getLogo());
                existing.setRemark(source.getRemark());
                updateEntity(existing, userId);
                targetId = existing.getId();
            }
            spaceVertexService.deleteByOwners(SpaceOwnerTypeEnum.ROOM.getKey(), Collections.singletonList(targetId));
            List<SpaceVertex> copies = new ArrayList<>();
            int orderNum = 1;
            for (SpaceVertex vertex : outline) {
                SpaceVertex copy = new SpaceVertex();
                copy.setOwnerType(SpaceOwnerTypeEnum.ROOM.getKey());
                copy.setOwnerId(targetId);
                copy.setLongitude(vertex.getLongitude());
                copy.setLatitude(vertex.getLatitude());
                copy.setOrderNum(orderNum++);
                copies.add(copy);
            }
            spaceVertexService.createEntity(copies, userId);
            synced++;
        }
        Map<String, Object> bean = new LinkedHashMap<>();
        bean.put("linkGroupId", groupId);
        bean.put("syncedFloorCount", synced);
        outputObject.setBean(bean);
    }
}
