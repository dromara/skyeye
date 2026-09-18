/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.browse.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.browse.dao.MemberBrowseHistoryDao;
import com.skyeye.browse.entity.MemberBrowseHistory;
import com.skyeye.browse.service.MemberBrowseHistoryService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "会员浏览足迹", groupName = "会员管理", tenant = TenantEnum.NO_ISOLATION)
public class MemberBrowseHistoryServiceImpl
    extends SkyeyeBusinessServiceImpl<MemberBrowseHistoryDao, MemberBrowseHistory>
    implements MemberBrowseHistoryService {

    @Override
    @IgnoreTenant
    public void recordBrowseHistory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String memberId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        String materialStoreId = params.get("materialStoreId").toString();
        if (StrUtil.isBlank(materialStoreId)) {
            materialStoreId = params.get("id").toString();
        }
        if (StrUtil.isBlank(materialStoreId)) {
            return;
        }
        String now = DateUtil.getTimeAndToString();
        QueryWrapper<MemberBrowseHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberBrowseHistory::getMemberId), memberId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberBrowseHistory::getMaterialStoreId), materialStoreId);
        MemberBrowseHistory exist = getOne(queryWrapper, false);
        if (exist != null) {
            exist.setGoodsName(StrUtil.blankToDefault(params.get("goodsName").toString(), exist.getGoodsName()));
            exist.setGoodsLogo(StrUtil.blankToDefault(params.get("goodsLogo").toString(), exist.getGoodsLogo()));
            exist.setPrice(StrUtil.blankToDefault(params.get("price").toString(), exist.getPrice()));
            exist.setStoreName(StrUtil.blankToDefault(params.get("storeName").toString(), exist.getStoreName()));
            exist.setMaterialId(StrUtil.blankToDefault(params.get("materialId").toString(), exist.getMaterialId()));
            exist.setStoreId(StrUtil.blankToDefault(params.get("storeId").toString(), exist.getStoreId()));
            exist.setViewCount((exist.getViewCount() == null ? 0 : exist.getViewCount()) + 1);
            exist.setLastViewTime(now);
            updateById(exist);
            outputObject.setBean(exist);
            return;
        }
        MemberBrowseHistory history = new MemberBrowseHistory();
        history.setMemberId(memberId);
        history.setMaterialStoreId(materialStoreId);
        history.setMaterialId(params.get("materialId").toString());
        history.setStoreId(params.get("storeId").toString());
        history.setGoodsName(params.get("goodsName").toString());
        history.setGoodsLogo(params.get("goodsLogo").toString());
        history.setPrice(params.get("price").toString());
        history.setStoreName(params.get("storeName").toString());
        history.setViewCount(CommonNumConstants.NUM_ONE);
        history.setLastViewTime(now);
        createEntity(history, memberId);
        outputObject.setBean(history);
    }

    @Override
    @IgnoreTenant
    public void queryMyBrowseHistoryList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String memberId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        QueryWrapper<MemberBrowseHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberBrowseHistory::getMemberId), memberId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(MemberBrowseHistory::getLastViewTime));
        List<MemberBrowseHistory> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public void clearMyBrowseHistory(InputObject inputObject, OutputObject outputObject) {
        String memberId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        QueryWrapper<MemberBrowseHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MemberBrowseHistory::getMemberId), memberId);
        remove(queryWrapper);
    }

    @Override
    @IgnoreTenant
    public void deleteMyBrowseHistoryById(InputObject inputObject, OutputObject outputObject) {
        String memberId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        String id = inputObject.getParams().get("id").toString();
        MemberBrowseHistory history = selectById(id);
        if (history == null || !StrUtil.equals(memberId, history.getMemberId())) {
            throw new CustomException("足迹不存在");
        }
        deleteById(id);
    }

}
