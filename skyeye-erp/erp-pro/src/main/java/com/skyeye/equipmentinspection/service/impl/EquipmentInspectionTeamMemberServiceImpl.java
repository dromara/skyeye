/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionTeamMemberDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionTeamMember;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTeamMemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionTeamMemberServiceImpl
 * @Description: 设备巡检班组人员服务层
 */
@Service
@SkyeyeService(name = "设备巡检班组人员", groupName = "设备巡检", allowDynamicAttrKey = false)
public class EquipmentInspectionTeamMemberServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionTeamMemberDao, EquipmentInspectionTeamMember>
    implements EquipmentInspectionTeamMemberService {

    @Override
    protected QueryWrapper<EquipmentInspectionTeamMember> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionTeamMember> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionTeamMember::getTeamId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        setStaffMationForMap(beans);
        return beans;
    }

    private void setStaffMationForMap(List<Map<String, Object>> beans) {
        List<String> staffIds = beans.stream()
            .map(bean -> bean.get("staffId"))
            .filter(ObjectUtil::isNotNull)
            .map(Object::toString)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(staffIds)) {
            return;
        }
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        beans.forEach(bean -> {
            if (bean.get("staffId") != null) {
                bean.put("staffMation", staffMap.get(bean.get("staffId").toString()));
            }
        });
    }

    @Override
    public void deleteMemberListByTeamId(String teamId) {
        if (StrUtil.isEmpty(teamId)) {
            return;
        }
        QueryWrapper<EquipmentInspectionTeamMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionTeamMember::getTeamId), teamId);
        remove(queryWrapper);
    }
}
