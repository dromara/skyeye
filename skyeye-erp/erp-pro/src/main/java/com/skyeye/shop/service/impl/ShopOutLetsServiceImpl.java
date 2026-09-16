/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.classenum.DepotOutState;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.service.DepotOutService;
import com.skyeye.entity.ErpOrderHead;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.rest.shop.service.IShopStoreService;
import com.skyeye.shop.dao.ShopOutLetsDao;
import com.skyeye.shop.entity.ShopOutLets;
import com.skyeye.shop.service.ShopOutLetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopOutLetsServiceImpl
 * @Description: 门店申领单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:07
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "门店申领单", groupName = "门店", flowable = true, orderApproval = true)
public class ShopOutLetsServiceImpl extends SkyeyeErpOrderServiceImpl<ShopOutLetsDao, ShopOutLets> implements ShopOutLetsService {

    @Autowired
    private DepotOutService depotOutService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private IShopStoreService iShopStoreService;

    @Override
    public QueryWrapper<ShopOutLets> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ShopOutLets> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
            commonPageInfo.setObjectId("-");
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderHead::getStoreId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        iShopStoreService.setMationForMap(beans, "storeId", "storeMation");
        // 业务员
        iAuthUserService.setMationForMap(beans, "salesman", "salesmanMation");
        return beans;
    }

    @Override
    public void validatorEntity(ShopOutLets entity) {
        entity.setOtherState(DepotOutState.NEED_OUT.getKey());
    }

    @Override
    public void createPrepose(ShopOutLets entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.OUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public ShopOutLets selectById(String id) {
        ShopOutLets shopOutLets = super.selectById(id);
        // 部门
        iDepmentService.setDataMation(shopOutLets, ShopOutLets::getDepartmentId);
        // 门店
        iShopStoreService.setDataMation(shopOutLets, ShopOutLets::getStoreId);
        // 业务员
        iAuthUserService.setDataMation(shopOutLets, ShopOutLets::getSalesman);
        return shopOutLets;
    }

    @Override
    public void queryShopOutLetsTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ShopOutLets shopOutLets = selectById(id);
        // 该门店申领单下的已经下达仓库出库单(审核通过)的数量
        Map<String, String> depotNumMap = depotOutService.calcMaterialNormsNumByFromId(shopOutLets.getId());
        // 设置未下达商品数量-----门店申领单数量 - 已出库数量
        super.setOrCheckOperNumber(shopOutLets.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        shopOutLets.setErpOrderItemList(shopOutLets.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(shopOutLets);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertShopOutLetsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotOut depotOut = inputObject.getParams(DepotOut.class);
        // 获取门店申领单状态
        ShopOutLets shopOutLets = selectById(depotOut.getId());
        if (ObjectUtil.isEmpty(shopOutLets)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(shopOutLets.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotOut.setFromId(depotOut.getId());
            depotOut.setFromTypeId(DepotOutFromType.SHOP_OUTLET.getKey());
            depotOut.setId(StrUtil.EMPTY);
            depotOutService.createEntity(depotOut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库出库单.");
        }
    }
}
