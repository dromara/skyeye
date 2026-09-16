/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import com.skyeye.entity.ErpOrderCommon;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.pick.classenum.OutLetState;
import com.skyeye.pick.classenum.RequisitionOutLetFromType;
import com.skyeye.pick.dao.RequisitionOutLetDao;
import com.skyeye.pick.entity.PickChild;
import com.skyeye.pick.entity.RequisitionMaterial;
import com.skyeye.pick.entity.RequisitionOutLet;
import com.skyeye.pick.service.RequisitionMaterialService;
import com.skyeye.pick.service.RequisitionOutLetService;
import com.skyeye.util.ErpOrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: RequisitionOutLetServiceImpl
 * @Description: 领料出库单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/26 20:36
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "领料出库单", groupName = "物料单", flowable = true, orderApproval = true)
public class RequisitionOutLetServiceImpl extends SkyeyeErpOrderServiceImpl<RequisitionOutLetDao, RequisitionOutLet> implements RequisitionOutLetService {

    @Autowired
    private RequisitionMaterialService requisitionMaterialService;

    @Autowired
    private DepotOutService depotOutService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private FarmService farmService;

    @Override
    public QueryWrapper<RequisitionOutLet> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<RequisitionOutLet> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 查询所有的，type为空或者不等于department和farm即可
        if (StrUtil.equals(commonPageInfo.getType(), "department")) {
            // 我所在部门
            String departmentId = InputObject.getLogParamsStatic().get("departmentId").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getDepartmentId), departmentId);
        } else if (StrUtil.equals(commonPageInfo.getType(), "farm")) {
            // 指定车间
            String departmentId = InputObject.getLogParamsStatic().get("departmentId").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getDepartmentId), departmentId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getFarmId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置领料需求单
        requisitionMaterialService.setOrderMationByFromId(beans, "fromId", "fromMation");
        farmService.setMationForMap(beans, "farmId", "farmMation");
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        return beans;
    }

    @Override
    public void validatorEntity(RequisitionOutLet entity) {
        entity.setOtherState(DepotOutState.NEED_OUT.getKey());
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(RequisitionOutLet entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.OUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public RequisitionOutLet selectById(String id) {
        RequisitionOutLet requisitionOutLet = super.selectById(id);
        // 部门
        iDepmentService.setDataMation(requisitionOutLet, RequisitionOutLet::getDepartmentId);
        // 车间
        farmService.setDataMation(requisitionOutLet, RequisitionOutLet::getFarmId);
        if (requisitionOutLet.getFromTypeId() == RequisitionOutLetFromType.REQUISITION_MATERIAL.getKey()) {
            // 领料需求单
            requisitionMaterialService.setDataMation(requisitionOutLet, RequisitionOutLet::getFromId);
        }
        return requisitionOutLet;
    }

    private void checkMaterialNorms(RequisitionOutLet entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前领料出库单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达领料出库单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == RequisitionOutLetFromType.REQUISITION_MATERIAL.getKey()) {
            // 领料需求单
            checkAndUpdateFromState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateFromState(RequisitionOutLet entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        RequisitionMaterial requisitionMaterial = requisitionMaterialService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(requisitionMaterial.getPickChildList())) {
            throw new CustomException("该领料单下未包含商品.");
        }
        List<String> fromNormsIds = requisitionMaterial.getPickChildList().stream()
            .map(PickChild::getNormsId).collect(Collectors.toList());
        super.checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);

        requisitionMaterial.getPickChildList().forEach(pickChild -> {
            // 领料需求单数量 - 当前单据数量 - 已经下达领料出库单的数量
            String surplusNum = ErpOrderUtil.checkOperNumber(pickChild.getNeedNum(), pickChild.getNormsId(), orderNormsNum, executeNum);
            if (setData) {
                pickChild.setNeedNum(surplusNum);
            }
        });
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<PickChild> pickChildList = requisitionMaterial.getPickChildList().stream()
                .filter(pickChild -> CalculationUtil.compareTo(pickChild.getNeedNum(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该领料需求单的商品已经全部生成了领料出库单，那说明已经完成
            if (CollectionUtil.isEmpty(pickChildList)) {
                requisitionMaterialService.editOtherState(requisitionMaterial.getId(), OutLetState.COMPLATE_OUTLET.getKey());
            } else {
                requisitionMaterialService.editOtherState(requisitionMaterial.getId(), OutLetState.PARTIAL_OUTLET.getKey());
            }
        }
    }

    @Override
    public void approvalEndIsSuccess(RequisitionOutLet entity) {
        RequisitionOutLet oldEntity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(oldEntity, true);
    }

    @Override
    public void queryRequisitionOutLetsTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        RequisitionOutLet requisitionOutLet = selectById(id);
        // 该领料出库单下的已经下达仓库出库单(审核通过)的数量
        Map<String, String> depotNumMap = depotOutService.calcMaterialNormsNumByFromId(requisitionOutLet.getId());
        // 设置未下达商品数量-----领料出库单数量 - 已出库数量
        super.setOrCheckOperNumber(requisitionOutLet.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        requisitionOutLet.setErpOrderItemList(requisitionOutLet.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(requisitionOutLet);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertRequisitionOutLetsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotOut depotOut = inputObject.getParams(DepotOut.class);
        // 获取领料出库单状态
        RequisitionOutLet requisitionOutLet = selectById(depotOut.getId());
        if (ObjectUtil.isEmpty(requisitionOutLet)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(requisitionOutLet.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotOut.setFromId(depotOut.getId());
            depotOut.setFromTypeId(DepotOutFromType.REQUISITION_OUTLET.getKey());
            depotOut.setId(StrUtil.EMPTY);
            depotOutService.createEntity(depotOut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库出库单.");
        }
    }
}
