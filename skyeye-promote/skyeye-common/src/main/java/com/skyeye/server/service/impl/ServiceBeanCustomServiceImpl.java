/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.coderule.entity.CodeRule;
import com.skyeye.coderule.service.CodeRuleService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.server.dao.ServiceBeanCustomDao;
import com.skyeye.server.entity.ServiceBean;
import com.skyeye.server.entity.ServiceBeanCustom;
import com.skyeye.server.service.ServiceBeanCustomService;
import com.skyeye.server.service.ServiceBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ServiceBeanCustomServiceImpl
 * @Description: 自定义服务管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/1/6 22:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "自定义服务管理", groupName = "系统公共模块", tenant = TenantEnum.NO_ISOLATION)
public class ServiceBeanCustomServiceImpl extends SkyeyeBusinessServiceImpl<ServiceBeanCustomDao, ServiceBeanCustom> implements ServiceBeanCustomService {

    @Autowired
    private ServiceBeanService serviceBeanService;

    @Autowired
    private CodeRuleService codeRuleService;

    @Override
    public void queryServiceBeanCustom(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String appId = params.get("appId").toString();
        ServiceBeanCustom serviceBeanCustom = selectServiceBeanCustom(appId, className);
        outputObject.setBean(serviceBeanCustom);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryServiceBeanCustomAiFormAssist(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String appId = params.get("appId").toString();
        ServiceBeanCustom serviceBeanCustom = getServiceBeanCustomEntity(appId, className);
        Integer aiFormAssist = serviceBeanCustom == null || serviceBeanCustom.getAiFormAssist() == null
            ? WhetherEnum.ENABLE_USING.getKey()
            : serviceBeanCustom.getAiFormAssist();
        Map<String, Object> bean = new HashMap<>();
        bean.put("appId", appId);
        bean.put("className", className);
        bean.put("aiFormAssist", aiFormAssist);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void editServiceBeanCustomAiFormAssist(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String appId = params.get("appId").toString();
        Integer aiFormAssist = Integer.parseInt(params.get("aiFormAssist").toString());
        String userId = inputObject.getLogParams().get("id").toString();

        ServiceBeanCustom existing = getServiceBeanCustomEntity(appId, className);
        if (existing == null) {
            ServiceBeanCustom bean = new ServiceBeanCustom();
            bean.setAppId(appId);
            bean.setClassName(className);
            bean.setAiFormAssist(aiFormAssist);
            createEntity(bean, userId);
        } else {
            UpdateWrapper<ServiceBeanCustom> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(MybatisPlusUtil.toColumns(ServiceBeanCustom::getId), existing.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(ServiceBeanCustom::getAiFormAssist), aiFormAssist);
            update(updateWrapper);
        }

        Map<String, Object> bean = new HashMap<>();
        bean.put("appId", appId);
        bean.put("className", className);
        bean.put("aiFormAssist", aiFormAssist);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private ServiceBeanCustom getServiceBeanCustomEntity(String appId, String className) {
        QueryWrapper<ServiceBeanCustom> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(appId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceBeanCustom::getAppId), appId);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceBeanCustom::getClassName), className);
        return getOne(queryWrapper, false);
    }

    @Override
    public ServiceBeanCustom selectServiceBeanCustom(String appId, String className) {
        ServiceBeanCustom serviceBeanCustom = getServiceBeanCustomEntity(appId, className);
        ServiceBean serviceBean = serviceBeanService.queryServiceClass(appId, className);
        if (serviceBeanCustom == null) {
            serviceBeanCustom = new ServiceBeanCustom();
        }
        serviceBeanCustom.setServiceBean(serviceBean);
        if (StrUtil.isNotEmpty(serviceBeanCustom.getCodeRuleId())) {
            CodeRule codeRule = codeRuleService.selectById(serviceBeanCustom.getCodeRuleId());
            serviceBeanCustom.setCodeRule(codeRule);
        }
        return serviceBeanCustom;
    }

    @Override
    public void deleteServiceBeanCustom(String appId, String className) {
        QueryWrapper<ServiceBeanCustom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceBeanCustom::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceBeanCustom::getClassName), className);
        remove(queryWrapper);
    }

    @Override
    public void builderByHandler(ServiceBeanCustom bean) {
        if (bean != null) {
            super.builderByHandler(bean);
        }
    }

}
