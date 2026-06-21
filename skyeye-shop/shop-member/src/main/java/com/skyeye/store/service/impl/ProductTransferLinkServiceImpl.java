/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.store.dao.ProductTransferLinkDao;
import com.skyeye.store.entity.ProductTransferLink;
import com.skyeye.store.service.ProductTransferLinkService;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @ClassName: ProductTransferLinkServiceImpl
 * @Description: 门店产品调拨明细服务层
 */
@Service
@SkyeyeService(name = "门店产品调拨明细", groupName = "门店产品调拨", manageShow = false)
public class ProductTransferLinkServiceImpl extends SkyeyeLinkDataServiceImpl<ProductTransferLinkDao, ProductTransferLink>
    implements ProductTransferLinkService {

    @Override
    protected void checkLinkList(String pId, List<ProductTransferLink> beans) {
        beans.forEach(link -> {
            if (StrUtil.isEmpty(link.getMaterialId()) || StrUtil.isEmpty(link.getNormsId()) || StrUtil.isEmpty(link.getOperNumber())) {
                throw new CustomException("产品、规格、调拨数量不能为空");
            }
            if (CalculationUtil.compareTo(link.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_TWO, RoundingMode.UP) <= 0) {
                throw new CustomException("调拨数量必须大于0");
            }
        });
        List<String> checkIds = beans.stream()
            .map(bean -> String.format(Locale.ROOT, "%s_%s", bean.getMaterialId(), bean.getNormsId()))
            .distinct()
            .collect(Collectors.toList());
        if (checkIds.size() != beans.size()) {
            throw new CustomException("存在重复的产品规格明细");
        }
    }

}
