/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.decorate.dao.StorePageCompDao;
import com.skyeye.decorate.entity.StorePageComp;
import com.skyeye.decorate.enums.StorePageCompType;
import com.skyeye.decorate.service.StorePageCompService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "门店装修组件", groupName = "门店装修", tenant = TenantEnum.NO_ISOLATION)
public class StorePageCompServiceImpl extends SkyeyeBusinessServiceImpl<StorePageCompDao, StorePageComp> implements StorePageCompService {

    @Override
    public List<StorePageComp> queryListByPageId(String pageId) {
        QueryWrapper<StorePageComp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(StorePageComp::getPageId), pageId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(StorePageComp::getOrderBy));
        return list(queryWrapper);
    }

    @Override
    public void deleteByPageId(String pageId) {
        QueryWrapper<StorePageComp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(StorePageComp::getPageId), pageId);
        remove(queryWrapper);
    }

    @Override
    public void createDefaultComps(String pageId, String storeId, String userId) {
        List<StorePageComp> list = new ArrayList<>();
        list.add(buildDefault(pageId, storeId, StorePageCompType.STORE_HEADER, "店头", 1));
        list.add(buildDefault(pageId, storeId, StorePageCompType.BANNER, "轮播", 2));
        list.add(buildDefault(pageId, storeId, StorePageCompType.NOTICE, "公告", 3));
        list.add(buildDefault(pageId, storeId, StorePageCompType.COUPON, "优惠券", 4));
        list.add(buildDefault(pageId, storeId, StorePageCompType.RECOMMEND, "店长推荐", 5));
        list.add(buildDefault(pageId, storeId, StorePageCompType.GOODS, "商品区", 6));
        createEntity(list, userId);
    }

    private StorePageComp buildDefault(String pageId, String storeId, StorePageCompType type, String title, int orderBy) {
        StorePageComp comp = new StorePageComp();
        comp.setPageId(pageId);
        comp.setStoreId(storeId);
        comp.setCompType(type.getKey());
        comp.setTitle(title);
        comp.setEnabled(EnableEnum.ENABLE_USING.getKey());
        comp.setOrderBy(orderBy);
        comp.setShowPc(WhetherEnum.ENABLE_USING.getKey());
        comp.setShowApp(WhetherEnum.ENABLE_USING.getKey());
        Map<String, Object> config = new HashMap<>();
        if (StorePageCompType.NOTICE.equals(type)) {
            config.put("text", "");
        } else if (StorePageCompType.RECOMMEND.equals(type)) {
            config.put("recommendIds", new ArrayList<>());
        } else if (StorePageCompType.GOODS.equals(type)) {
            config.put("showCategory", true);
        } else if (StorePageCompType.STORE_HEADER.equals(type)) {
            config.put("showFollow", true);
            config.put("showHours", true);
            config.put("showRemark", true);
        }
        comp.setConfigJson(config);
        return comp;
    }
}
