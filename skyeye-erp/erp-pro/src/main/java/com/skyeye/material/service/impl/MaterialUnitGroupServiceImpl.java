/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.material.dao.MaterialUnitGroupDao;
import com.skyeye.material.entity.unit.MaterialUnit;
import com.skyeye.material.entity.unit.MaterialUnitGroup;
import com.skyeye.material.service.MaterialUnitGroupService;
import com.skyeye.material.service.MaterialUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: MaterialUnitServiceImpl
 * @Description: 计量单位分组服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 10:15
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "计量单位分组", groupName = "计量单位", allowDynamicAttrKey = false)
public class MaterialUnitGroupServiceImpl extends SkyeyeBusinessServiceImpl<MaterialUnitGroupDao, MaterialUnitGroup> implements MaterialUnitGroupService {

    @Autowired
    private MaterialUnitService materialUnitService;

    @Override
    protected void writePostpose(MaterialUnitGroup entity, String userId) {
        super.writePostpose(entity, userId);
        // 保存计量单位信息
        materialUnitService.saveBatchList(entity.getUnitList(), userId, entity.getId());
    }

    @Override
    public void deletePostpose(String id) {
        // 删除计量单位信息
        materialUnitService.deleteByGroupId(id);
    }

    @Override
    public MaterialUnitGroup getDataFromDb(String id) {
        MaterialUnitGroup materialUnitGroup = super.getDataFromDb(id);
        // 查询单位信息
        materialUnitGroup.setUnitList(materialUnitService.queryUnitListByGroupId(id));
        return materialUnitGroup;
    }

    @Override
    public MaterialUnitGroup selectById(String id) {
        MaterialUnitGroup materialUnitGroup = super.selectById(id);
        materialUnitGroup.getUnitList().forEach(unit -> {
            Map<String, Object> baseUnitMation = new HashMap<>();
            baseUnitMation.put("name", WhetherEnum.getName(unit.getBaseUnit()));
            unit.setBaseUnitMation(baseUnitMation);
        });
        return materialUnitGroup;
    }

    @Override
    protected List<MaterialUnitGroup> getDataFromDb(List<String> idList) {
        List<MaterialUnitGroup> materialUnitGroups = super.getDataFromDb(idList);
        List<String> ids = materialUnitGroups.stream().map(MaterialUnitGroup::getId).collect(Collectors.toList());
        // 查询单位信息
        Map<String, List<MaterialUnit>> groupUnitMap = materialUnitService.queryUnitListByGroupId(ids);
        materialUnitGroups.forEach(materialUnitGroup -> {
            materialUnitGroup.setUnitList(groupUnitMap.get(materialUnitGroup.getId()));
        });
        return materialUnitGroups;
    }

    /**
     * 获取计量单位展示为下拉框
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllMaterialUnitList(InputObject inputObject, OutputObject outputObject) {
        List<MaterialUnitGroup> materialUnitGroups = queryAllData();
        if (CollectionUtil.isNotEmpty(materialUnitGroups)) {
            List<String> ids = materialUnitGroups.stream().map(MaterialUnitGroup::getId).collect(Collectors.toList());
            Map<String, List<MaterialUnit>> groupUnitMap = materialUnitService.queryUnitListByGroupId(ids);
            materialUnitGroups.forEach(group ->
                group.setUnitList(groupUnitMap.getOrDefault(group.getId(), new ArrayList<>())));
        }
        outputObject.setBeans(materialUnitGroups);
        outputObject.settotal(materialUnitGroups.size());
    }

    @Override
    public MaterialUnitGroup ensureDefaultPieceGroup(String userId) {
        List<MaterialUnitGroup> groups = queryAllData();
        if (CollectionUtil.isNotEmpty(groups)) {
            List<String> ids = groups.stream().map(MaterialUnitGroup::getId).collect(Collectors.toList());
            Map<String, List<MaterialUnit>> groupUnitMap = materialUnitService.queryUnitListByGroupId(ids);
            for (MaterialUnitGroup group : groups) {
                List<MaterialUnit> unitList = groupUnitMap.getOrDefault(group.getId(), new ArrayList<>());
                if (CollectionUtil.isNotEmpty(unitList)) {
                    group.setUnitList(unitList);
                    return group;
                }
            }
            // 有分组但没有单位：补一条「件」
            MaterialUnitGroup first = groups.get(0);
            MaterialUnit unit = new MaterialUnit();
            unit.setName("件");
            unit.setNumber(1);
            unit.setBaseUnit(WhetherEnum.ENABLE_USING.getKey());
            first.setUnitList(Collections.singletonList(unit));
            updateEntity(first, userId);
            return selectById(first.getId());
        }
        MaterialUnitGroup group = new MaterialUnitGroup();
        group.setName("商城默认单位");
        MaterialUnit unit = new MaterialUnit();
        unit.setName("件");
        unit.setNumber(1);
        unit.setBaseUnit(WhetherEnum.ENABLE_USING.getKey());
        group.setUnitList(Collections.singletonList(unit));
        String id = createEntity(group, userId);
        return selectById(id);
    }

}
