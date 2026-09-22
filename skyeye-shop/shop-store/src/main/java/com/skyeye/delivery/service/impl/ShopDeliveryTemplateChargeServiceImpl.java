/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.delivery.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.delivery.dao.ShopDeliveryTemplateChargeDao;
import com.skyeye.delivery.entity.ShopDeliveryTemplate;
import com.skyeye.delivery.entity.ShopDeliveryTemplateCharge;
import com.skyeye.delivery.service.ShopDeliveryTemplateChargeService;
import com.skyeye.delivery.service.ShopDeliveryTemplateService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopDeliveryTemplateChargeServiceImpl
 * @Description: 快递运费模板计费配置信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "快递运费模板计费配置信息管理", groupName = "快递运费模板计费配置信息管理")
public class ShopDeliveryTemplateChargeServiceImpl extends SkyeyeBusinessServiceImpl<ShopDeliveryTemplateChargeDao, ShopDeliveryTemplateCharge> implements ShopDeliveryTemplateChargeService {

    @Autowired
    private ShopDeliveryTemplateService shopDeliveryTemplateService;

    @Override
    public QueryWrapper<ShopDeliveryTemplateCharge> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ShopDeliveryTemplateCharge> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String storeId = commonPageInfo.getObjectId();
        if (StrUtil.isNotEmpty(storeId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopDeliveryTemplateCharge::getStoreId), storeId);
        }
        return queryWrapper;
    }

    @Override
    protected QueryWrapper<ShopDeliveryTemplateCharge> getQueryWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<ShopDeliveryTemplateCharge> queryWrapper = super.getQueryWrapper(tableSelectInfo);
        String storeId = tableSelectInfo.getObjectId();
        if (StrUtil.isNotEmpty(storeId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopDeliveryTemplateCharge::getStoreId), storeId);
        }
        return queryWrapper;
    }

    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return new ArrayList<>();
        }
        shopDeliveryTemplateService.setMationForMap(beans, "templateId", "templateMation");
        // 分页查询时获取数据
        return beans;
    }

    @Override
    public void validatorEntity(ShopDeliveryTemplateCharge shopDeliveryTemplateCharge) {
        super.validatorEntity(shopDeliveryTemplateCharge);
        // 判断模板是否存在
        ShopDeliveryTemplate shopDeliveryTemplate = shopDeliveryTemplateService.selectById(shopDeliveryTemplateCharge.getTemplateId());
        // 判断shopDeliveryTemplate是否为空，如果为空则抛出异常
        if (StrUtil.isEmpty(shopDeliveryTemplate.getId())) {
            throw new CustomException("模板不存在，请刷新后重试！");
        }
    }

    @Override
    public ShopDeliveryTemplateCharge selectById(String id) {
        ShopDeliveryTemplateCharge charge = super.selectById(id);
        shopDeliveryTemplateService.setDataMation(charge, ShopDeliveryTemplateCharge::getTemplateId);
        return charge;
    }
}
