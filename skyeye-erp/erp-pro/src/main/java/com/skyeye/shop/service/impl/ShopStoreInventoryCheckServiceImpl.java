/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.exception.CustomException;
import com.skyeye.shop.dao.ShopStoreInventoryCheckDao;
import com.skyeye.shop.dao.ShopStoreInventoryCheckDetailDao;
import com.skyeye.shop.entity.ShopStoreInventoryCheck;
import com.skyeye.shop.entity.ShopStoreInventoryCheckDetail;
import com.skyeye.shop.entity.StoreInventoryCheckItem;
import com.skyeye.shop.service.ShopStoreInventoryCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "门店库存盘点历史", groupName = "门店物料库存", manageShow = false)
public class ShopStoreInventoryCheckServiceImpl
    extends SkyeyeBusinessServiceImpl<ShopStoreInventoryCheckDao, ShopStoreInventoryCheck>
    implements ShopStoreInventoryCheckService {

    @Autowired
    private ShopStoreInventoryCheckDetailDao shopStoreInventoryCheckDetailDao;

    @Override
    public void saveInventoryCheckHistory(String storeId, List<StoreInventoryCheckItem> itemList) {
        if (StrUtil.isBlank(storeId) || CollectionUtil.isEmpty(itemList)) {
            return;
        }
        Map<String, Object> user = InputObject.getLogParamsStatic();
        String userId = MapUtil.getStr(user, "id");
        String userName = MapUtil.getStr(user, "userName");
        if (StrUtil.isBlank(userName)) {
            userName = MapUtil.getStr(user, "name");
        }

        ShopStoreInventoryCheck check = new ShopStoreInventoryCheck();
        check.setStoreId(storeId);
        check.setItemCount(itemList.size());
        check.setOperName(userName);
        String parentId = createEntity(check, userId);

        List<ShopStoreInventoryCheckDetail> details = new ArrayList<>();
        for (StoreInventoryCheckItem item : itemList) {
            String book = StrUtil.blankToDefault(item.getBookStock(), CommonNumConstants.NUM_ZERO.toString());
            String real = StrUtil.blankToDefault(item.getRealNumber(), CommonNumConstants.NUM_ZERO.toString());
            String diff = CalculationUtil.subtract(real, book, ErpConstants.NUM_AFTER_DOT);
            ShopStoreInventoryCheckDetail detail = new ShopStoreInventoryCheckDetail();
            detail.setId(ToolUtil.getSurFaceId());
            detail.setParentId(parentId);
            detail.setMaterialId(item.getMaterialId());
            detail.setMaterialName(item.getMaterialName());
            detail.setNormsId(item.getNormsId());
            detail.setNormsName(item.getNormsName());
            detail.setBookStock(book);
            detail.setRealNumber(real);
            detail.setDiffNumber(diff);
            detail.setProfitNum(StrUtil.blankToDefault(item.getProfitNum(), CommonNumConstants.NUM_ZERO.toString()));
            detail.setLossNum(StrUtil.blankToDefault(item.getLossNum(), CommonNumConstants.NUM_ZERO.toString()));
            detail.setProfitNormsCode(StrUtil.blankToDefault(item.getProfitNormsCode(), StrUtil.EMPTY));
            detail.setLossNormsCode(StrUtil.blankToDefault(item.getLossNormsCode(), StrUtil.EMPTY));
            details.add(detail);
        }
        for (ShopStoreInventoryCheckDetail detail : details) {
            shopStoreInventoryCheckDetailDao.insert(detail);
        }
    }

    @Override
    @IgnoreTenant
    public void queryStoreInventoryCheckHistoryList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String storeId = pageInfo.getCustomParamsMapStr("storeId");
        if (StrUtil.isBlank(storeId)) {
            storeId = pageInfo.getObjectId();
        }
        if (StrUtil.isBlank(storeId)) {
            throw new CustomException("请选择门店");
        }
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        QueryWrapper<ShopStoreInventoryCheck> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(ShopStoreInventoryCheck::getStoreId), storeId);
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(ShopStoreInventoryCheck::getCreateTime));
        List<ShopStoreInventoryCheck> list = list(wrapper);
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryStoreInventoryCheckHistoryDetail(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = MapUtil.getStr(params, "id");
        if (StrUtil.isBlank(id)) {
            throw new CustomException("缺少盘点记录id");
        }
        ShopStoreInventoryCheck check = selectById(id);
        if (check == null) {
            throw new CustomException("盘点记录不存在");
        }
        QueryWrapper<ShopStoreInventoryCheckDetail> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(ShopStoreInventoryCheckDetail::getParentId), id);
        List<ShopStoreInventoryCheckDetail> details = shopStoreInventoryCheckDetailDao.selectList(wrapper);
        check.setDetailList(details);
        outputObject.setBean(check);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
