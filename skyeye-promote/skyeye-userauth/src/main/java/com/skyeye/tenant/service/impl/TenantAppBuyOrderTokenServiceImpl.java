/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.tenant.dao.TenantAppBuyOrderTokenDao;
import com.skyeye.tenant.entity.TenantAppBuyOrderToken;
import com.skyeye.tenant.service.TenantAppBuyOrderTokenService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "订单-购买Token明细", groupName = "租户管理", manageShow = false, tenant = TenantEnum.PLATE)
public class TenantAppBuyOrderTokenServiceImpl extends SkyeyeBusinessServiceImpl<TenantAppBuyOrderTokenDao, TenantAppBuyOrderToken> implements TenantAppBuyOrderTokenService {

    @Override
    public void saveList(String parentId, List<TenantAppBuyOrderToken> beans) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (TenantAppBuyOrderToken item : beans) {
                item.setParentId(parentId);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<TenantAppBuyOrderToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrderToken::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<TenantAppBuyOrderToken> selectByParentId(String parentId) {
        QueryWrapper<TenantAppBuyOrderToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrderToken::getParentId), parentId);
        return list(queryWrapper);
    }

}
