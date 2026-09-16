/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.other.service.impl;

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
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.classenum.DepotOutState;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.service.DepotOutService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.other.dao.OtherOutLetsDao;
import com.skyeye.other.entity.OtherOutLets;
import com.skyeye.other.service.OtherOutLetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: OtherOutLetsServiceImpl
 * @Description: 其他出库单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:07
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "其他出库单", groupName = "其他订单模块", flowable = true, orderApproval = true)
public class OtherOutLetsServiceImpl extends SkyeyeErpOrderServiceImpl<OtherOutLetsDao, OtherOutLets> implements OtherOutLetsService {

    @Autowired
    private DepotOutService depotOutService;

    @Override
    public void validatorEntity(OtherOutLets entity) {
        entity.setOtherState(DepotOutState.NEED_OUT.getKey());
    }

    @Override
    public void createPrepose(OtherOutLets entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.OUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void queryOtherOutLetsTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        OtherOutLets otherOutLets = selectById(id);
        // 该其他出库单下的已经下达仓库出库单(审核通过)的数量
        Map<String, String> depotNumMap = depotOutService.calcMaterialNormsNumByFromId(otherOutLets.getId());
        // 设置未下达商品数量-----其他出库单数量 - 已出库数量
        super.setOrCheckOperNumber(otherOutLets.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        otherOutLets.setErpOrderItemList(otherOutLets.getErpOrderItemList().stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber()) 
                    ? CommonNumConstants.NUM_ZERO.toString() 
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(otherOutLets);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertOtherOutLetsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotOut depotOut = inputObject.getParams(DepotOut.class);
        // 获取其他出库单状态
        OtherOutLets otherOutLets = selectById(depotOut.getId());
        if (ObjectUtil.isEmpty(otherOutLets)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(otherOutLets.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotOut.setFromId(depotOut.getId());
            depotOut.setFromTypeId(DepotOutFromType.OTHER_OUTLET.getKey());
            depotOut.setId(StrUtil.EMPTY);
            depotOutService.createEntity(depotOut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库出库单.");
        }
    }
}
