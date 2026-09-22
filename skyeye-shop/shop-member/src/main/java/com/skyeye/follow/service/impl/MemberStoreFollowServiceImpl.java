/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.follow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.follow.dao.MemberStoreFollowDao;
import com.skyeye.follow.entity.MemberStoreFollow;
import com.skyeye.follow.service.MemberStoreFollowService;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "会员门店关注", groupName = "会员管理", tenant = TenantEnum.NO_ISOLATION)
public class MemberStoreFollowServiceImpl extends SkyeyeBusinessServiceImpl<MemberStoreFollowDao, MemberStoreFollow> implements MemberStoreFollowService {

    private static final int PREVIEW_GOODS_LIMIT = 5;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private IShopMaterialNormsService iShopMaterialNormsService;

    @Override
    public void toggleStoreFollow(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String memberId = MapUtil.getStr(inputObject.getLogParams(), CommonConstants.ID);
        String storeId = MapUtil.getStr(params, "storeId");
        QueryWrapper<MemberStoreFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberStoreFollow::getMemberId), memberId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberStoreFollow::getStoreId), storeId);
        MemberStoreFollow exist = getOne(queryWrapper, false);

        Map<String, Object> result = new HashMap<>();
        if (exist != null) {
            // 取消关注
            deleteById(exist.getId());
            result.put("followed", false);
            outputObject.setBean(result);
            return;
        }
        // 关注
        MemberStoreFollow follow = new MemberStoreFollow();
        follow.setMemberId(memberId);
        follow.setStoreId(storeId);
        createEntity(follow, memberId);
        result.put("followed", true);
        outputObject.setBean(result);
    }

    @Override
    protected QueryWrapper<MemberStoreFollow> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<MemberStoreFollow> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String memberId = MapUtil.getStr(InputObject.getLogParamsStatic(), CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberStoreFollow::getMemberId), memberId);
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        shopStoreService.setMationForMap(beans, "storeId", "storeMation");
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        List<String> storeIds = beans.stream()
            .map(item -> MapUtil.getStr(item, "storeId"))
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, Object> previewMap = iShopMaterialNormsService.queryShopMaterialPreviewByStoreIds(storeIds, PREVIEW_GOODS_LIMIT);
        beans.forEach(item -> {
            String storeId = MapUtil.getStr(item, "storeId");
            Object goods = previewMap.get(storeId);
            if (goods instanceof List) {
                item.put("goodsList", goods);
            } else if (goods != null) {
                item.put("goodsList", JSONUtil.parseArray(goods));
            } else {
                item.put("goodsList", new ArrayList<>());
            }
        });
        return beans;
    }

    @Override
    public void checkStoreFollow(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String memberId = MapUtil.getStr(inputObject.getLogParams(), CommonConstants.ID);
        String storeId = MapUtil.getStr(params, "storeId");
        QueryWrapper<MemberStoreFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberStoreFollow::getMemberId), memberId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberStoreFollow::getStoreId), storeId);
        MemberStoreFollow exist = getOne(queryWrapper, false);
        Map<String, Object> result = new HashMap<>();
        result.put("followed", exist != null);
        outputObject.setBean(result);
    }

}
