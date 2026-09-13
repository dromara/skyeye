/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.attr.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.attr.classenum.AttrKeyDataType;
import com.skyeye.attr.dao.AttrDefinitionCustomDao;
import com.skyeye.attr.entity.AttrDefinition;
import com.skyeye.attr.entity.AttrDefinitionCustom;
import com.skyeye.attr.service.AttrDefinitionCustomService;
import com.skyeye.attr.service.AttrDefinitionService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.business.entity.BusinessApi;
import com.skyeye.business.service.BusinessApiService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dsform.entity.DsFormComponent;
import com.skyeye.dsform.service.DsFormComponentService;
import com.skyeye.server.entity.ServiceBean;
import com.skyeye.server.service.ServiceBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AttrDefinitionCustomServiceImpl
 * @Description: 用户自定义服务类属性服务类
 * @author: skyeye云系列--卫志强
 * @date: 2022/12/30 10:57
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "自定义服务管理", groupName = "系统公共模块", tenant = TenantEnum.NO_ISOLATION)
public class AttrDefinitionCustomServiceImpl extends SkyeyeBusinessServiceImpl<AttrDefinitionCustomDao, AttrDefinitionCustom> implements AttrDefinitionCustomService {

    @Autowired
    private AttrDefinitionService attrDefinitionService;

    @Autowired
    private DsFormComponentService dsFormComponentService;

    @Autowired
    private BusinessApiService businessApiService;

    @Autowired
    private ServiceBeanService serviceBeanService;

    @Override
    public void writePostpose(AttrDefinitionCustom entity, String userId) {
        super.writePostpose(entity, userId);
        businessApiService.deleteByObjectId(entity.getId());
        if (entity.getDataType() != null && entity.getDataType().equals(AttrKeyDataType.CUSTOM_API.getKey())) {
            // 保存属性关联的自定义的api接口
            BusinessApi businessApi = entity.getBusinessApi();
            businessApi.setObjectId(entity.getId());
            businessApi.setObjectKey(getServiceClassName());
            businessApiService.createEntity(businessApi, userId);
        }
    }

    @Override
    public List<AttrDefinitionCustom> queryAttrDefinitionCustomList(String appId, String className, List<String> attrKey) {
        if (CollectionUtil.isEmpty(attrKey)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<AttrDefinitionCustom> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinitionCustom::getAppId), appId);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinitionCustom::getClassName), className);
        wrapper.in(MybatisPlusUtil.toColumns(AttrDefinitionCustom::getAttrKey), attrKey);
        List<AttrDefinitionCustom> attrDefinitionCustomList = list(wrapper);
        // 获取关联的组件信息
        List<String> componentIdList = attrDefinitionCustomList.stream()
            .filter(attrDefinitionCustom -> StrUtil.isNotEmpty(attrDefinitionCustom.getComponentId()))
            .map(AttrDefinitionCustom::getComponentId).distinct().collect(Collectors.toList());
        Map<String, DsFormComponent> componentMap = dsFormComponentService.selectMapByIds(componentIdList);
        attrDefinitionCustomList.forEach(attrDefinitionCustom -> {
            if (StrUtil.isNotEmpty(attrDefinitionCustom.getComponentId())) {
                attrDefinitionCustom.setDsFormComponent(componentMap.get(attrDefinitionCustom.getComponentId()));
            }
        });
        // 查询属性关联的自定义的api接口
        List<String> ids = attrDefinitionCustomList.stream()
            .filter(bean -> bean.getDataType() != null && bean.getDataType().equals(AttrKeyDataType.CUSTOM_API.getKey()))
            .map(AttrDefinitionCustom::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(ids)) {
            Map<String, BusinessApi> businessApiMap = businessApiService.selectByObjectIds(ids);
            attrDefinitionCustomList.forEach(attrDefinitionCustom -> {
                if (attrDefinitionCustom.getDataType() != null && attrDefinitionCustom.getDataType().equals(AttrKeyDataType.CUSTOM_API.getKey())) {
                    attrDefinitionCustom.setBusinessApi(businessApiMap.get(attrDefinitionCustom.getId()));
                }
            });
        }
        return attrDefinitionCustomList;
    }

    @Override
    public Map<String, AttrDefinitionCustom> queryAttrDefinitionCustomMap(String appId, String className, List<String> attrKey) {
        List<AttrDefinitionCustom> attrDefinitionCustomList = queryAttrDefinitionCustomList(appId, className, attrKey);
        return attrDefinitionCustomList.stream().collect(Collectors.toMap(AttrDefinitionCustom::getAttrKey, item -> item));
    }

    @Override
    public AttrDefinitionCustom queryAttrDefinitionCustom(String appId, String className, String attrKey) {
        QueryWrapper<AttrDefinitionCustom> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinitionCustom::getAppId), appId);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinitionCustom::getClassName), className);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinitionCustom::getAttrKey), attrKey);
        AttrDefinitionCustom attrDefinitionCustom = getOne(wrapper);
        if (ObjectUtil.isNotEmpty(attrDefinitionCustom)) {
            if (StrUtil.isNotEmpty(attrDefinitionCustom.getComponentId())) {
                // 查询组件信息
                DsFormComponent dsFormComponent = dsFormComponentService.selectById(attrDefinitionCustom.getComponentId());
                attrDefinitionCustom.setDsFormComponent(dsFormComponent);
            }
            // 查询属性关联的自定义的api接口
            if (attrDefinitionCustom.getDataType() != null && attrDefinitionCustom.getDataType().equals(AttrKeyDataType.CUSTOM_API.getKey())) {
                attrDefinitionCustom.setBusinessApi(businessApiService.selectByObjectId(attrDefinitionCustom.getId()));
            }
        }
        return attrDefinitionCustom;
    }

    @Override
    public void queryAttrDefinitionCustom(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String attrKey = params.get("attrKey").toString();
        String appId = params.get("appId").toString();
        // 获取属性的原始信息
        AttrDefinition attrDefinition = attrDefinitionService.queryAttrDefinition(appId, className, attrKey);
        // 获取属性的自定义信息
        AttrDefinitionCustom attrDefinitionCustom = queryAttrDefinitionCustom(appId, className, attrKey);
        if (ObjectUtil.isEmpty(attrDefinitionCustom)) {
            attrDefinitionCustom = new AttrDefinitionCustom();
            attrDefinitionCustom.setName(attrDefinition.getName());
            attrDefinitionCustom.setRemark(attrDefinition.getRemark());
            attrDefinitionCustom.setAttrKey(attrDefinition.getAttrKey());
        }
        attrDefinitionCustom.setEnumClassStr(attrDefinition.getEnumClassStr());
        attrDefinitionCustom.setWhetherInputParams(attrDefinition.getWhetherInputParams());
        outputObject.setBean(attrDefinitionCustom);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 删除自定义属性信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void deleteAttrDefinitionCustom(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String attrKey = params.get("attrKey").toString();
        String appId = params.get("appId").toString();

        AttrDefinitionCustom attrDefinitionCustom = queryAttrDefinitionCustom(appId, className, attrKey);
        if (ObjectUtil.isEmpty(attrDefinitionCustom)) {
            return;
        }
        // 删除属性关联的自定义的api接口
        if (attrDefinitionCustom.getDataType() != null && attrDefinitionCustom.getDataType().equals(AttrKeyDataType.CUSTOM_API.getKey())) {
            businessApiService.deleteByObjectId(attrDefinitionCustom.getId());
        }
        removeById(attrDefinitionCustom.getId());
    }

    @Override
    public void setDsFormComponentUseNum(List<Map<String, Object>> beans) {
        List<String> dsFormComponentId = beans.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(dsFormComponentId)) {
            return;
        }
        QueryWrapper<AttrDefinitionCustom> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(AttrDefinitionCustom::getComponentId), dsFormComponentId);
        queryWrapper.select(CommonConstants.ID, MybatisPlusUtil.toColumns(AttrDefinitionCustom::getComponentId));
        List<AttrDefinitionCustom> list = list(queryWrapper);
        Map<String, Long> collect = list.stream().collect(Collectors.groupingBy(AttrDefinitionCustom::getComponentId, Collectors.counting()));
        beans.forEach(bean -> {
            String id = bean.get("id").toString();
            bean.put("attrUseNum", collect.get(id));
        });
    }

    /**
     * 根据组件id查询正在使用该组件的服务类信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAttrByComponentId(InputObject inputObject, OutputObject outputObject) {
        String componentId = inputObject.getParams().get("componentId").toString();
        if (StrUtil.isEmpty(componentId)) {
            return;
        }
        QueryWrapper<AttrDefinitionCustom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AttrDefinitionCustom::getComponentId), componentId);
        List<AttrDefinitionCustom> attrDefinitionCustoms = list(queryWrapper);
        List<String> classNames = attrDefinitionCustoms.stream().map(bean -> bean.getAppId() + bean.getClassName()).collect(Collectors.toList());
        // 查询服务类信息
        Map<String, ServiceBean> serviceBeanMap = serviceBeanService.queryServiceClass(classNames);
        attrDefinitionCustoms.forEach(attrDefinitionCustom -> {
            String className = attrDefinitionCustom.getAppId() + attrDefinitionCustom.getClassName();
            ServiceBean serviceBean = serviceBeanMap.get(className);
            if (ObjectUtil.isEmpty(serviceBean)) {
                attrDefinitionCustom.setWhetherDelete(true);
                return;
            }
            // 判断属性是否在服务类的原始属性中，如果不在，则说明该自定义属性可以删除
            List<String> attrKeyList = serviceBean.getAttrDefinitionList().stream().map(AttrDefinition::getAttrKey)
                .distinct().collect(Collectors.toList());
            if (attrKeyList.contains(attrDefinitionCustom.getAttrKey())) {
                attrDefinitionCustom.setWhetherDelete(false);
            } else {
                attrDefinitionCustom.setWhetherDelete(true);
            }
            attrDefinitionCustom.setServiceBean(serviceBean);
        });
        outputObject.setBeans(attrDefinitionCustoms);
        outputObject.settotal(attrDefinitionCustoms.size());
    }

    @Override
    public void deleteAttrDefinitionCustom(String appId, String className) {
        QueryWrapper<AttrDefinitionCustom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AttrDefinitionCustom::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AttrDefinitionCustom::getClassName), className);
        remove(queryWrapper);
    }
}
