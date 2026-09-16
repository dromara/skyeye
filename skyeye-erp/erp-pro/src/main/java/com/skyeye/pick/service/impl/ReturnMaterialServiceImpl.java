/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.exception.CustomException;
import com.skyeye.pick.classenum.PutState;
import com.skyeye.pick.classenum.ReturnPutFromType;
import com.skyeye.pick.dao.ReturnMaterialDao;
import com.skyeye.pick.entity.ReturnMaterial;
import com.skyeye.pick.entity.ReturnPut;
import com.skyeye.pick.service.ReturnMaterialService;
import com.skyeye.pick.service.ReturnPutService;
import com.skyeye.util.ErpOrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ReturnMaterialServiceImpl
 * @Description: 退料申请单管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/20 10:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "退料单", groupName = "物料单", flowable = true, orderApproval = true)
public class ReturnMaterialServiceImpl extends ErpPickServiceImpl<ReturnMaterialDao, ReturnMaterial> implements ReturnMaterialService {

    @Autowired
    private ReturnPutService returnPutService;

    @Override
    public void createPrepose(ReturnMaterial entity) {
        super.createPrepose(entity);
        entity.setOtherState(PutState.NEED_PUT.getKey());
    }

    @Override
    public void queryReturnMaterialTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ReturnMaterial returnMaterial = selectById(id);
        // 该退料单下的已经下达退料入库单(审核通过)的数量
        Map<String, String> executeNum = returnPutService.calcMaterialNormsNumByFromId(returnMaterial.getId());
        // 设置未下达商品数量-----退料单数量 - 退料入库单数量
        returnMaterial.getPickChildList().forEach(pickChild -> {
            // 退料单数量 - 已经下达退料入库单的数量
            String surplusNum = ErpOrderUtil.checkOperNumber(pickChild.getNeedNum(), pickChild.getNormsId(), executeNum);
            pickChild.setNeedNum(surplusNum);
        });
        // 过滤掉数量为0的商品信息
        returnMaterial.setPickChildList(returnMaterial.getPickChildList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getNeedNum(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(returnMaterial);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertReturnMaterialToTurnOut(InputObject inputObject, OutputObject outputObject) {
        ReturnPut returnPut = inputObject.getParams(ReturnPut.class);
        // 获取退料单状态
        ReturnMaterial returnMaterial = selectById(returnPut.getId());
        if (ObjectUtil.isEmpty(returnMaterial)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到退料入库单
        if (FlowableStateEnum.PASS.getKey().equals(returnMaterial.getState()) &&
            (returnMaterial.getOtherState() == PutState.NEED_PUT.getKey()
                || returnMaterial.getOtherState() == PutState.PARTIAL_PUT.getKey())) {
            String userId = inputObject.getLogParams().get("id").toString();
            returnPut.setFromId(returnPut.getId());
            returnPut.setFromTypeId(ReturnPutFromType.RETURN_PUT.getKey());
            returnPut.setId(StrUtil.EMPTY);
            returnPutService.createEntity(returnPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达退料入库单.");
        }
    }
}
