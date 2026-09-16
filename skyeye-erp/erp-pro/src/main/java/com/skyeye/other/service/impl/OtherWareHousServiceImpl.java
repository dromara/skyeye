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
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.other.dao.OtherWareHousDao;
import com.skyeye.other.entity.OtherWareHous;
import com.skyeye.other.service.OtherWareHousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: OtherWareHousServiceImpl
 * @Description: 其他入库单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:08
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "其他入库单", groupName = "其他订单模块", flowable = true, orderApproval = true)
public class OtherWareHousServiceImpl extends SkyeyeErpOrderServiceImpl<OtherWareHousDao, OtherWareHous> implements OtherWareHousService {

    @Autowired
    private DepotPutService depotPutService;

    @Override
    public void validatorEntity(OtherWareHous entity) {
        entity.setOtherState(DepotPutState.NEED_PUT.getKey());
    }

    @Override
    public void createPrepose(OtherWareHous entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void queryOtherWareHousTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        OtherWareHous otherWareHous = selectById(id);
        // 该其他入库单下的已经下达仓库入库单(审核通过)的数量
        Map<String, String> depotNumMap = depotPutService.calcMaterialNormsNumByFromId(otherWareHous.getId());
        // 设置未下达商品数量-----其他入库单数量 - 已入库数量
        super.setOrCheckOperNumber(otherWareHous.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        otherWareHous.setErpOrderItemList(otherWareHous.getErpOrderItemList().stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber()) 
                    ? CommonNumConstants.NUM_ZERO.toString() 
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(otherWareHous);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertOtherWareHousToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        // 获取其他入库单状态
        OtherWareHous otherWareHous = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(otherWareHous)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库入库单
        if (FlowableStateEnum.PASS.getKey().equals(otherWareHous.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.OTHER_WARE_HOUS.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }
}
