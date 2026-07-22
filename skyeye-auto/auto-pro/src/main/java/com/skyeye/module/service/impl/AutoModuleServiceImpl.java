/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.module.classenum.ModuleAuthEnum;
import com.skyeye.module.dao.AutoModuleDao;
import com.skyeye.module.entity.AutoModule;
import com.skyeye.module.service.AutoModuleService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoModuleServiceImpl
 * @Description: 项目模块管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/19 8:22
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "项目模块管理", groupName = "项目模块管理", teamAuth = true)
public class AutoModuleServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoModuleDao, AutoModule> implements AutoModuleService {

    @Override
    public Class getAuthEnumClass() {
        return ModuleAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(ModuleAuthEnum.ADD.getKey(), ModuleAuthEnum.EDIT.getKey(), ModuleAuthEnum.DELETE.getKey());
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (tenantEnable) {
            commonPageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoModuleList(commonPageInfo);
        List<String> ids = beans.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return beans;
        }
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 查询子节点信息(包含当前节点)
        List<String> childIds = skyeyeBaseMapper.queryAllChildIdsByParentId(ids, tenantId);
        beans = selectMapByIds(childIds).values().stream().map(bean -> BeanUtil.beanToMap(bean)).collect(Collectors.toList());
        beans = beans.stream()
            .sorted(Comparator.comparing((Map<String, Object> bean) -> bean.get("createTime").toString()).reversed())
            .collect(Collectors.toList());
        beans.forEach(bean -> {
            bean.put("lay_is_open", true);
        });
        return beans;
    }

    @Override
    public void queryAutoModuleForTree(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        String objectKey = params.get("objectKey").toString();
        QueryWrapper<AutoModule> queryWrapper = new QueryWrapper();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoModule::getObjectKey), objectKey);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoModule::getObjectId), objectId);
        List<AutoModule> result = list(queryWrapper);
        // 转为树
        List<Tree<String>> treeNodes = TreeUtil.build(result, String.valueOf(CommonNumConstants.NUM_ZERO), new TreeNodeConfig(),
            (treeNode, tree) -> {
                tree.setId(treeNode.getId());
                tree.setParentId(treeNode.getParentId());
                tree.setName(treeNode.getName());
                tree.putExtra("isParent", true);
            });
        outputObject.setBeans(treeNodes);
        outputObject.settotal(treeNodes.size());
    }

    @Override
    public void queryFirstAutoModuleList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        String objectKey = params.get("objectKey").toString();
        if (StrUtil.isEmpty(objectId)) {
            return;
        }
        QueryWrapper<AutoModule> queryWrapper = new QueryWrapper();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoModule::getObjectKey), objectKey);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoModule::getObjectId), objectId);
        // parentId 为 String，必须用 "0"：若传 Integer 0，MySQL 会按数字比较，空串/非数字串会被当成 0 误命中
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoModule::getParentId), String.valueOf(CommonNumConstants.NUM_ZERO));
        List<AutoModule> result = list(queryWrapper);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public List<String> queryAllChildIdsByParentId(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return CollectionUtil.newArrayList();
        }
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        return skyeyeBaseMapper.queryAllChildIdsByParentId(ids, tenantId);
    }
}
