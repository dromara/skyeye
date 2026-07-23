/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.tenant.dao.TenantAppLinkDo;
import com.skyeye.tenant.entity.TenantApp;
import com.skyeye.tenant.entity.TenantAppLink;
import com.skyeye.tenant.service.TenantAppLinkService;
import com.skyeye.tenant.service.TenantAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TenantAppLinkServiceImpl
 * @Description: 租户与应用的关系管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 20:44
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "租户与应用的关系管理", groupName = "租户管理", tenant = TenantEnum.PLATE)
public class TenantAppLinkServiceImpl extends SkyeyeBusinessServiceImpl<TenantAppLinkDo, TenantAppLink> implements TenantAppLinkService {

    @Autowired
    private TenantAppService tenantAppService;

    @Override
    public void saveTenantAppLink(String tenantId, String appId, Integer year) {
        QueryWrapper<TenantAppLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppLink::getBuyTenantId), tenantId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppLink::getAppId), appId);
        TenantAppLink tenantAppLink = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(tenantAppLink)) {
            TenantAppLink newTenantAppLink = new TenantAppLink();
            newTenantAppLink.setBuyTenantId(tenantId);
            newTenantAppLink.setAppId(appId);
            String currentTime = DateUtil.getYmdTimeAndToString();
            newTenantAppLink.setStartTime(currentTime);
            String endTime = DateUtil.getDate(currentTime, year);
            newTenantAppLink.setEndTime(endTime);
            createEntity(newTenantAppLink, StrUtil.EMPTY);
        } else {
            String endTime = DateUtil.getDate(tenantAppLink.getEndTime(), year);
            tenantAppLink.setEndTime(endTime);
            updateEntity(tenantAppLink, StrUtil.EMPTY);
        }
    }

    @Override
    @IgnoreTenant
    public List<TenantAppLink> selectByTenantId(String tenantId) {
        QueryWrapper<TenantAppLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppLink::getBuyTenantId), tenantId);
        List<TenantAppLink> tenantAppLinkList = list(queryWrapper);
        return tenantAppLinkList;
    }

    @Override
    @IgnoreTenant
    public void queryTenantAppLinkList(InputObject inputObject, OutputObject outputObject) {
        String tenantId = inputObject.getParams().get("tenantId").toString();
        List<TenantAppLink> tenantAppLinkList = selectByTenantId(tenantId);
        if (CollectionUtil.isNotEmpty(tenantAppLinkList)) {
            List<String> appIds = tenantAppLinkList.stream().map(TenantAppLink::getAppId).collect(Collectors.toList());
            Map<String, TenantApp> tenantAppMap = tenantAppService.queryTenantAppByAppId(appIds.toArray(new String[]{}));
            tenantAppLinkList.forEach(tenantAppLink -> {
                tenantAppLink.setAppMation(tenantAppMap.get(tenantAppLink.getAppId()));
            });
        }
        outputObject.setBeans(tenantAppLinkList);
        outputObject.settotal(tenantAppLinkList.size());
    }

    /**
     * 按购买租户删除全部应用关联（解散组织时调用）。
     *
     * @param tenantId 购买租户id
     */
    @Override
    @IgnoreTenant
    public void deleteByTenantId(String tenantId) {
        if (StrUtil.isBlank(tenantId)) {
            return;
        }
        QueryWrapper<TenantAppLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppLink::getBuyTenantId), tenantId);
        remove(queryWrapper);
    }

}
