/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.portal.classenum.PortalProductFeatureIconEnum;
import com.skyeye.portal.dao.PortalProductFeatureDao;
import com.skyeye.portal.entity.PortalProductFeature;
import com.skyeye.portal.service.PortalProductFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * 官网产品功能矩阵服务实现
 */
@Service
@SkyeyeService(name = "官网产品功能矩阵", groupName = "门户管理", tenant = TenantEnum.PLATE, allowDynamicAttrKey = false)
public class PortalProductFeatureServiceImpl extends SkyeyeBusinessServiceImpl<PortalProductFeatureDao, PortalProductFeature> implements PortalProductFeatureService {

    @Autowired
    private RedisCache redisCache;

    private void fillIconCode(PortalProductFeature entity) {
        if (entity == null) {
            return;
        }
        entity.setIconCode(PortalProductFeatureIconEnum.getIconCodeByKey(entity.getIcon()));
    }

    @Override
    protected QueryWrapper<PortalProductFeature> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PortalProductFeature> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(PortalProductFeature::getOrderBy))
            .orderByDesc(MybatisPlusUtil.toColumns(PortalProductFeature::getCreateTime));
        return queryWrapper;
    }

    @Override
    protected void writePostpose(PortalProductFeature entity, String userId) {
        super.writePostpose(entity, userId);
        jedisClientService.del(getCacheKey());
    }

    @Override
    protected void deletePostpose(PortalProductFeature entity) {
        jedisClientService.del(getCacheKey());
    }

    @Override
    @IgnoreTenant
    public void queryEnabledPortalProductFeatureList(InputObject inputObject, OutputObject outputObject) {
        String cacheKey = getCacheKey();
        List<PortalProductFeature> result = redisCache.getList(cacheKey, key -> {
            QueryWrapper<PortalProductFeature> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(PortalProductFeature::getEnabled), EnableEnum.ENABLE_USING.getKey())
                .orderByDesc(MybatisPlusUtil.toColumns(PortalProductFeature::getOrderBy))
                .orderByDesc(MybatisPlusUtil.toColumns(PortalProductFeature::getCreateTime));
            return list(queryWrapper);
        }, RedisConstants.A_YEAR_SECONDS, PortalProductFeature.class);
        result.forEach(this::fillIconCode);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    private String getCacheKey() {
        return String.format(Locale.ROOT, "portal:product:feature");
    }
}
