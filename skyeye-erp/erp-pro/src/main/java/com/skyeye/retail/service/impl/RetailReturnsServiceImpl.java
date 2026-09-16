/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.retail.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.retail.dao.RetailReturnsDao;
import com.skyeye.retail.entity.RetailReturns;
import com.skyeye.retail.service.RetailReturnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: RetailReturnsServiceImpl
 * @Description: 零售退货单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "零售退货单", groupName = "零售模块", flowable = true, orderApproval = true)
public class RetailReturnsServiceImpl extends SkyeyeErpOrderServiceImpl<RetailReturnsDao, RetailReturns> implements RetailReturnsService {

    @Autowired
    private DepotPutService depotPutService;

    @Override
    public void validatorEntity(RetailReturns entity) {
        entity.setOtherState(DepotPutState.NEED_PUT.getKey());
    }

    @Override
    public void createPrepose(RetailReturns entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void queryRetailReturnsTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        RetailReturns retailReturns = selectById(id);
        // 该零售退货单下的已经下达仓库入库单(审核通过)的数量
        Map<String, String> depotNumMap = depotPutService.calcMaterialNormsNumByFromId(retailReturns.getId());
        // 设置未下达商品数量-----零售退货单数量 - 已入库数量
        super.setOrCheckOperNumber(retailReturns.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        retailReturns.setErpOrderItemList(retailReturns.getErpOrderItemList().stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber()) 
                    ? CommonNumConstants.NUM_ZERO.toString() 
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(retailReturns);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertRetailReturnsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        // 获取零售退货单状态
        RetailReturns retailReturns = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(retailReturns)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库入库单
        if (FlowableStateEnum.PASS.getKey().equals(retailReturns.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.RETAIL_RETURNS.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }
}
