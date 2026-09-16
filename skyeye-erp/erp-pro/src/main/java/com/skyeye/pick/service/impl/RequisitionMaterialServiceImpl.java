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
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.exception.CustomException;
import com.skyeye.machin.classenum.MachinPickStateEnum;
import com.skyeye.machin.entity.Machin;
import com.skyeye.machin.service.MachinService;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.pick.classenum.OutLetState;
import com.skyeye.pick.classenum.RequisitionOutLetFromType;
import com.skyeye.pick.dao.RequisitionMaterialDao;
import com.skyeye.pick.entity.PickChild;
import com.skyeye.pick.entity.RequisitionMaterial;
import com.skyeye.pick.entity.RequisitionOutLet;
import com.skyeye.pick.service.DepartmentStockService;
import com.skyeye.pick.service.RequisitionMaterialService;
import com.skyeye.pick.service.RequisitionOutLetService;
import com.skyeye.util.ErpOrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: RequisitionMaterialServiceImpl
 * @Description: 领料申请单管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/27 12:50
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "领料单", groupName = "物料单", flowable = true, orderApproval = true)
public class RequisitionMaterialServiceImpl extends ErpPickServiceImpl<RequisitionMaterialDao, RequisitionMaterial> implements RequisitionMaterialService {

    @Autowired
    private MachinService machinService;

    @Autowired
    private RequisitionOutLetService requisitionOutLetService;

    @Autowired
    private DepartmentStockService departmentStockService;

    @Override
    public void createPrepose(RequisitionMaterial entity) {
        super.createPrepose(entity);
        entity.setOtherState(OutLetState.NEED_OUTLET.getKey());
    }

    @Override
    public void approvalEndIsSuccess(RequisitionMaterial entity) {
        if (StrUtil.isNotEmpty(entity.getFromId())) {
            machinService.editPickStateById(entity.getFromId(), MachinPickStateEnum.PICKED.getKey());
        }
        RequisitionMaterial oldEntity = selectById(entity.getId());
        // 增加在途库存，记录关联的加工单ID（如果fromId是加工单ID）
        String machinId = StrUtil.isNotEmpty(oldEntity.getFromId()) ? oldEntity.getFromId() : null;
        // 领料单的归属部门和物料的归属主体是两个不同的维度，如果关联了加工单，那么领取的物料就按照加工单所属的部门走，否则按照领料部门走
        String departmentId = oldEntity.getDepartmentId();
        if (StrUtil.isNotEmpty(machinId)) {
            Machin machin = machinService.selectById(machinId);
            departmentId = machin.getDepartmentId();
        }
        for (PickChild pickChild : oldEntity.getPickChildList()) {
            departmentStockService.updateDepartmentStock(departmentId, oldEntity.getFarmId(),
                pickChild.getMaterialId(), pickChild.getNormsId(), pickChild.getNeedNum(), DepotPutOutType.PUT.getKey(), MaterialNormsStockType.IN_TRANSIT_STOCK.getKey(), machinId);
        }
    }

    @Override
    public void queryRequisitionMaterialTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        RequisitionMaterial requisitionMaterial = selectById(id);
        // 该领料单下的已经下达领料出库单(审核通过)的数量
        Map<String, String> executeNum = requisitionOutLetService.calcMaterialNormsNumByFromId(requisitionMaterial.getId());
        // 设置未下达商品数量-----领料单数量 - 领料出库单数量
        requisitionMaterial.getPickChildList().forEach(pickChild -> {
            // 领料单数量 - 已经下达领料出库单的数量
            String surplusNum = ErpOrderUtil.checkOperNumber(pickChild.getNeedNum(), pickChild.getNormsId(), executeNum);
            pickChild.setNeedNum(surplusNum);
        });
        // 过滤掉数量为0的商品信息
        requisitionMaterial.setPickChildList(requisitionMaterial.getPickChildList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getNeedNum(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(requisitionMaterial);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertRequisitionMaterialToTurnOut(InputObject inputObject, OutputObject outputObject) {
        RequisitionOutLet requisitionOutLet = inputObject.getParams(RequisitionOutLet.class);
        // 获取领料单状态
        RequisitionMaterial requisitionMaterial = selectById(requisitionOutLet.getId());
        if (ObjectUtil.isEmpty(requisitionMaterial)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到领料出库单
        if (FlowableStateEnum.PASS.getKey().equals(requisitionMaterial.getState()) &&
            (requisitionMaterial.getOtherState() == OutLetState.NEED_OUTLET.getKey()
                || requisitionMaterial.getOtherState() == OutLetState.PARTIAL_OUTLET.getKey())) {
            String userId = inputObject.getLogParams().get("id").toString();
            requisitionOutLet.setFromId(requisitionOutLet.getId());
            requisitionOutLet.setFromTypeId(RequisitionOutLetFromType.REQUISITION_MATERIAL.getKey());
            requisitionOutLet.setId(StrUtil.EMPTY);
            requisitionOutLetService.createEntity(requisitionOutLet, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达领料出库单.");
        }
    }
}
