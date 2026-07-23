/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.ActFlowDao;
import com.skyeye.eve.entity.ActFlowMation;
import com.skyeye.eve.service.ActFlowService;
import com.skyeye.eve.service.ITenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ActFlowServiceImpl
 * @Description: 流程模型管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/4 22:53
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "流程模型管理", groupName = "流程模型管理")
public class ActFlowServiceImpl extends SkyeyeBusinessServiceImpl<ActFlowDao, ActFlowMation> implements ActFlowService {

    @Autowired
    private ActivitiModelService activitiModelService;

    @Autowired
    private ITenantService iTenantService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置流程模型信息
        activitiModelService.setActivitiModelList(beans);
        return beans;
    }

    @Override
    protected void validatorEntity(ActFlowMation entity) {
        QueryWrapper<ActFlowMation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ActFlowMation::getModelKey), entity.getModelKey());
        if (StrUtil.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        if (this.count(queryWrapper) > 0) {
            throw new IllegalArgumentException("模型Key已存在");
        }
    }

    @Override
    public void createPrepose(ActFlowMation entity) {
        if (StrUtil.isEmpty(entity.getModelId())) {
            // 新增工作流模型信息
            String modelId = activitiModelService.insertNewActivitiModel(entity.getFlowName(), entity.getModelKey());
            entity.setModelId(modelId);
        } else {
            // 复制工作流模型信息
            String newModelId = activitiModelService.copyActivitiModel(entity.getModelId(), entity.getFlowName(), entity.getModelKey());
            entity.setModelId(newModelId);
        }
    }

    @Override
    public void updatePostpose(ActFlowMation entity, String userId) {
        // 修改工作流模型信息
        activitiModelService.editModelByModelId(entity.getModelId(), entity.getFlowName(), entity.getModelKey());
    }

    @Override
    public void deletePreExecution(String id) {
        ActFlowMation actFlowMation = selectById(id);
        // 删除模型信息
        activitiModelService.deleteActivitiModelById(actFlowMation.getModelId());
    }

    /**
     * 根据id批量获取工作流模型信息
     *
     * @param ids
     * @return
     */
    @Override
    public Map<String, ActFlowMation> actIdToFlowNameByIds(List<String> ids) {
        ids = ids.stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<ActFlowMation> actFlowMationList = selectByIds(ids.toArray(new String[]{}));
        Map<String, ActFlowMation> actFlowMationMap = actFlowMationList.stream().collect(Collectors.toMap(bean -> bean.getId(), bean -> bean));
        ids.forEach(id -> {
            if (!actFlowMationMap.containsKey(id)) {
                actFlowMationMap.put(id, new ActFlowMation());
            }
        });
        return actFlowMationMap;
    }

    @Override
    public ActFlowMation getActFlowByModelKey(String modelKey) {
        QueryWrapper<ActFlowMation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ActFlowMation::getModelKey), modelKey);
        return getOne(queryWrapper, false);
    }

    @Override
    @IgnoreTenant
    public void queryActFlowListByClassName(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isEmpty(pageInfo.getServiceClassName())) {
            throw new IllegalArgumentException("服务类名不能为空");
        }
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        QueryWrapper<ActFlowMation> queryWrapper = super.getQueryWrapper(pageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ActFlowMation::getApplyAppId), pageInfo.getServiceAppId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ActFlowMation::getApplyServiceClassName), pageInfo.getServiceClassName());
        List<ActFlowMation> list = list(queryWrapper);
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(list), null);
        if (!tenantEnable) {
            // 未开启多租户时，设置流程模型信息
            activitiModelService.setActivitiModelList(beans);
        } else {
            // 开启多租户时，设置租户信息
            iTenantService.setMationForMap(beans, "tenantId", "tenantMation");
            activitiModelService.setActivitiModelListForTenant(beans);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void queryAllActFlowListByClassName(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String serviceClassName = params.get("serviceClassName").toString();
        String appId = params.get("appId").toString();
        QueryWrapper<ActFlowMation> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(appId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ActFlowMation::getApplyAppId), appId);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(ActFlowMation::getApplyServiceClassName), serviceClassName);
        List<ActFlowMation> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void copyActFlowMationById(InputObject inputObject, OutputObject outputObject) {
        ActFlowMation actFlowMation = inputObject.getParams(ActFlowMation.class);

        ActFlowMation oldActFlowMation = selectById(actFlowMation.getId());
        if (oldActFlowMation == null) {
            throw new IllegalArgumentException("流程模型不存在");
        }
        oldActFlowMation.setFlowName(actFlowMation.getFlowName());
        oldActFlowMation.setModelKey(actFlowMation.getModelKey());
        oldActFlowMation.setApplyAppId(actFlowMation.getApplyAppId());
        oldActFlowMation.setApplyServiceClassName(actFlowMation.getApplyServiceClassName());
        oldActFlowMation.setId(null);
        oldActFlowMation.setTenantId(null);

        String userId = inputObject.getLogParams().get("id").toString();
        createEntity(oldActFlowMation, userId);
    }

    @Override
    @IgnoreTenant
    public List<ActFlowMation> queryActFlowMationByTenantId(String tenantId) {
        if (StrUtil.isEmpty(tenantId)) {
            return Collections.emptyList();
        }
        QueryWrapper<ActFlowMation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ActFlowMation::getTenantId), tenantId);
        return list(queryWrapper);
    }

    /**
     * 解散租户时按租户清理工作流定义及 Activiti 模型。
     *
     * @param tenantId 租户id
     */
    @Override
    @IgnoreTenant
    public void deleteByTenantId(String tenantId) {
        if (StrUtil.isEmpty(tenantId)) {
            return;
        }
        List<ActFlowMation> flowList = queryActFlowMationByTenantId(tenantId);
        if (CollectionUtil.isEmpty(flowList)) {
            return;
        }
        // 先删 Activiti 模型，再删业务表记录
        for (ActFlowMation flow : flowList) {
            if (StrUtil.isNotEmpty(flow.getModelId())) {
                try {
                    activitiModelService.deleteActivitiModelById(flow.getModelId());
                } catch (Exception ignored) {
                    // 模型可能已不存在，继续清理业务表
                }
            }
        }
        List<String> ids = flowList.stream().map(ActFlowMation::getId).collect(Collectors.toList());
        deleteById(ids);
    }

}
