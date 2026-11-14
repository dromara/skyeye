/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.classenum.DepotLevelVisualTypeEnum;
import com.skyeye.depot.dao.DepotLevelDao;
import com.skyeye.depot.entity.DepotLevel;
import com.skyeye.depot.service.DepotLevelService;
import com.skyeye.depot.service.DepotLevelValService;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DepotLevelServiceImpl
 * @Description: 仓库级别服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/5 22:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "仓库级别管理", groupName = "仓库级别管理")
public class DepotLevelServiceImpl extends SkyeyeBusinessServiceImpl<DepotLevelDao, DepotLevel> implements DepotLevelService {

    @Autowired
    private DepotLevelValService depotLevelValService;

    @Override
    public void validatorEntity(DepotLevel entity) {
        super.validatorEntity(entity);
        if (entity.getVisualType() == null) {
            entity.setVisualType(DepotLevelVisualTypeEnum.CUSTOM.getKey());
        }
        QueryWrapper<DepotLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevel::getDepotId), entity.getDepotId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevel::getParentId), entity.getParentId());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        long count = count(queryWrapper);
        if (count != 0) {
            throw new CustomException("该父节点下已存在子节点，一个节点下面只允许有一个子节点");
        }
    }

    @Override
    public void deletePostpose(DepotLevel entity) {
        List<DepotLevel> depotLevelList = queryDepotLevelListByDepotId(entity.getDepotId());
        if (CollectionUtil.isEmpty(depotLevelList)) {
            return;
        }
        // 将数据转化为树的形式，方便进行父id重新赋值
        List<Map<String, Object>> depotLevelMapList = JSONUtil.toList(JSONUtil.toJsonStr(depotLevelList), null);
        depotLevelMapList = ToolUtil.listToTree(depotLevelMapList, "id", "parentId", "children");
        // 删除父节点id不是0的所有节点
        depotLevelMapList = depotLevelMapList.stream().filter(depotLevel -> !StrUtil.equals(CommonNumConstants.NUM_ZERO.toString(), depotLevel.get("parentId").toString()))
            .collect(Collectors.toList());
        List<String> ids = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(depotLevelMapList)) {
            // 递归找到所有待删除的子节点id
            findChildId(depotLevelMapList, ids);
            deleteById(ids);
        }
        ids.add(entity.getId());
        depotLevelValService.deleteDepotLevelValListByDepotLevelId(ids.toArray(new String[]{}));
    }

    private void findChildId(List<Map<String, Object>> depotLevelMapList, List<String> ids) {
        depotLevelMapList.forEach(depotLevel -> {
            ids.add(depotLevel.get("id").toString());
            List<Map<String, Object>> children = (List<Map<String, Object>>) depotLevel.get("children");
            if (CollectionUtil.isNotEmpty(children)) {
                findChildId(children, ids);
            }
        });
    }

    private List<DepotLevel> queryDepotLevelListByDepotId(String depotId) {
        QueryWrapper<DepotLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevel::getDepotId), depotId);
        return list(queryWrapper);
    }

    @Override
    public void queryDepotLevelByDepotId(InputObject inputObject, OutputObject outputObject) {
        String depotId = inputObject.getParams().get("depotId").toString();
        List<DepotLevel> depotLevelList = queryDepotLevelListByDepotId(depotId);
        if (CollectionUtil.isEmpty(depotLevelList)) {
            return;
        }
        // 将数据转化为树的形式，方便进行父id重新赋值
        List<Map<String, Object>> depotLevelMapList = JSONUtil.toList(JSONUtil.toJsonStr(depotLevelList), null);
        depotLevelMapList = ToolUtil.listToTree(depotLevelMapList, "id", "parentId", "children");
        outputObject.setBeans(depotLevelMapList);
        outputObject.settotal(depotLevelMapList.size());
    }

    @Override
    public DepotLevel queryChildDepotLevelById(String depotId, String id) {
        QueryWrapper<DepotLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevel::getDepotId), depotId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotLevel::getParentId), id);
        return getOne(queryWrapper, false);
    }
}
