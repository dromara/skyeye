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
import com.skyeye.school.building.entity.TeachBuilding;
import com.skyeye.school.building.floorenum.FloorInfoEnum;
import com.skyeye.school.building.floorenum.SpaceOwnerTypeEnum;
import com.skyeye.school.building.service.FloorInfoService;
import com.skyeye.school.building.service.RoomOccupantService;
import com.skyeye.school.building.service.SpaceVertexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
        } else if (nodeType == FloorInfoEnum.SERVICE_INFO_ENUM.getKey()) {
            // 新增的节点是服务
            setDisabledStatus(floorInfo, parentId, FloorInfoEnum.SERVICE_INFO_ENUM.getKey());
        } else {
            // 新增的节点是楼层
            floorInfo.setLevel(FloorInfoEnum.FLOOR_INFO_ENUM.getKey());
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
}
