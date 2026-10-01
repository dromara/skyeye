/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.decorate.dao.StoreBannerDao;
import com.skyeye.decorate.entity.StoreBanner;
import com.skyeye.decorate.service.StoreBannerService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 门店轮播：写/删/分页走低代码基类；仅 C 端启用列表与门店隔离逻辑自定义。
 */
@Service
@SkyeyeService(name = "门店轮播", groupName = "门店装修", tenant = TenantEnum.NO_ISOLATION)
public class StoreBannerServiceImpl extends SkyeyeBusinessServiceImpl<StoreBannerDao, StoreBanner> implements StoreBannerService {

    @Autowired
    private StoreDecorateAuthHelper storeDecorateAuthHelper;

    @Override
    public void validatorEntity(StoreBanner entity) {
        super.validatorEntity(entity);
        if (StrUtil.isNotEmpty(entity.getName()) && entity.getName().length() > 100) {
            throw new CustomException("轮播标题过长");
        }
        if (entity.getOrderBy() == null || entity.getOrderBy() < -128 || entity.getOrderBy() > 127) {
            throw new CustomException("轮播排序值超出范围");
        }
        if (StrUtil.isBlank(entity.getPcLogo()) && StrUtil.isBlank(entity.getAppLogo())) {
            throw new CustomException("请至少上传一张PC或移动端轮播图");
        }
        storeDecorateAuthHelper.assertStoreDecorateAccess(entity.getStoreId());
    }

    @Override
    @IgnoreTenant
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        super.queryPageList(inputObject, outputObject);
    }

    @Override
    public QueryWrapper<StoreBanner> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<StoreBanner> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // objectId = 门店 id（与优惠券门店列表一致）
        if (StrUtil.isBlank(commonPageInfo.getObjectId())) {
            throw new CustomException("请选择门店");
        }
        storeDecorateAuthHelper.assertStoreDecorateAccess(commonPageInfo.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(StoreBanner::getStoreId), commonPageInfo.getObjectId());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(StoreBanner::getOrderBy));
        return queryWrapper;
    }

    @Override
    @IgnoreTenant
    public void saveOrUpdateEntity(InputObject inputObject, OutputObject outputObject) {
        super.saveOrUpdateEntity(inputObject, outputObject);
    }

    @Override
    @IgnoreTenant
    public void deleteByIds(InputObject inputObject, OutputObject outputObject) {
        super.deleteByIds(inputObject, outputObject);
    }

    @Override
    protected void deletePreExecution(StoreBanner entity) {
        if (entity == null || StrUtil.isBlank(entity.getStoreId())) {
            throw new CustomException("轮播不存在");
        }
        storeDecorateAuthHelper.assertStoreDecorateAccess(entity.getStoreId());
    }

    @Override
    protected void deletePreExecution(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        List<StoreBanner> list = selectByIds(ids.toArray(new String[0]));
        if (CollectionUtil.isEmpty(list) || list.size() != ids.size()) {
            throw new CustomException("轮播不存在或不完整");
        }
        for (StoreBanner banner : list) {
            storeDecorateAuthHelper.assertStoreDecorateAccess(banner.getStoreId());
        }
    }

    @Override
    @IgnoreTenant
    public void queryEnabledStoreBannerList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        List<StoreBanner> list = listEnabledByStoreId(storeId);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public List<StoreBanner> listEnabledByStoreId(String storeId) {
        QueryWrapper<StoreBanner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(StoreBanner::getStoreId), storeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(StoreBanner::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(StoreBanner::getOrderBy));
        return list(queryWrapper);
    }
}
