/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionTeamDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionTeam;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTeamMemberService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTeamService;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionTeamServiceImpl
 * @Description: 设备巡检班组服务层
 */
@Service
@SkyeyeService(name = "设备巡检班组", groupName = "设备巡检", allowDynamicAttrKey = false)
public class EquipmentInspectionTeamServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionTeamDao, EquipmentInspectionTeam>
    implements EquipmentInspectionTeamService {

    @Autowired
    private EquipmentInspectionTeamMemberService equipmentInspectionTeamMemberService;

    @Override
    protected void validatorEntity(EquipmentInspectionTeam entity) {
        QueryWrapper<EquipmentInspectionTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionTeam::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(EquipmentInspectionTeam::getTeamCode), entity.getTeamCode()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        EquipmentInspectionTeam existed = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(existed)) {
            throw new CustomException("名称/编码已存在.");
        }
    }

    @Override
    protected QueryWrapper<EquipmentInspectionTeam> getQueryWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<EquipmentInspectionTeam> queryWrapper = super.getQueryWrapper(tableSelectInfo);
        if (tableSelectInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionTeam::getEnabled), tableSelectInfo.getEnabled());
        }
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(EquipmentInspectionTeam::getOrderBy));
        return queryWrapper;
    }

    @Override
    protected void deletePostpose(EquipmentInspectionTeam entity) {
        equipmentInspectionTeamMemberService.deleteMemberListByTeamId(entity.getId());
    }

    @Override
    public void queryAllEquipmentInspectionTeamList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String enabled = params.get("enabled").toString();
        QueryWrapper<EquipmentInspectionTeam> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(enabled)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionTeam::getEnabled), enabled);
        }
        List<EquipmentInspectionTeam> teamList = list(queryWrapper);
        outputObject.setBeans(teamList);
        outputObject.settotal(teamList.size());
    }
}
