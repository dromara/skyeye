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
import com.skyeye.pick.classenum.PatchOutLetFromType;
import com.skyeye.pick.dao.PatchOutLetDao;
import com.skyeye.pick.entity.PatchMaterial;
import com.skyeye.pick.entity.PatchOutLet;
import com.skyeye.pick.entity.PickChild;
import com.skyeye.pick.service.PatchMaterialService;
import com.skyeye.pick.service.PatchOutLetService;
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
 * @ClassName: PatchOutLetServiceImpl
 * @Description: 补料出库单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/26 20:41
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "补料出库单", groupName = "物料单", flowable = true, orderApproval = true)
public class PatchOutLetServiceImpl extends SkyeyeErpOrderServiceImpl<PatchOutLetDao, PatchOutLet> implements PatchOutLetService {

    @Autowired
    private PatchMaterialService patchMaterialService;

    @Autowired
    private DepotOutService depotOutService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private FarmService farmService;

    @Override
    public QueryWrapper<PatchOutLet> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PatchOutLet> queryWrapper = super.getQueryWrapper(commonPageInfo);
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
        // 设置补料需求单
        patchMaterialService.setOrderMationByFromId(beans, "fromId", "fromMation");
        farmService.setMationForMap(beans, "farmId", "farmMation");
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        return beans;
    }

    @Override
    public void validatorEntity(PatchOutLet entity) {
        entity.setOtherState(DepotOutState.NEED_OUT.getKey());
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(PatchOutLet entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.OUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public PatchOutLet selectById(String id) {
        PatchOutLet requisitionOutLet = super.selectById(id);
        // 部门
        iDepmentService.setDataMation(requisitionOutLet, PatchOutLet::getDepartmentId);
        // 车间
        farmService.setDataMation(requisitionOutLet, PatchOutLet::getFarmId);

        if (requisitionOutLet.getFromTypeId() == PatchOutLetFromType.PATCH_OUT_LET.getKey()) {
            // 补料需求单
            patchMaterialService.setDataMation(requisitionOutLet, PatchOutLet::getFromId);
        }

        return requisitionOutLet;
    }

    private void checkMaterialNorms(PatchOutLet entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前补料出库单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达补料出库单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == PatchOutLetFromType.PATCH_OUT_LET.getKey()) {
            // 补料需求单
            checkAndUpdateFromState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateFromState(PatchOutLet entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        PatchMaterial patchMaterial = patchMaterialService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(patchMaterial.getPickChildList())) {
            throw new CustomException("该补料单下未包含商品.");
        }
        List<String> fromNormsIds = patchMaterial.getPickChildList().stream()
            .map(PickChild::getNormsId).collect(Collectors.toList());
        super.checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);

        patchMaterial.getPickChildList().forEach(pickChild -> {
            // 补料需求单数量 - 当前单据数量 - 已经下达补料出库单的数量
            String surplusNum = ErpOrderUtil.checkOperNumber(pickChild.getNeedNum(), pickChild.getNormsId(), orderNormsNum, executeNum);
            if (setData) {
                pickChild.setNeedNum(surplusNum);
            }
        });
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<PickChild> pickChildList = patchMaterial.getPickChildList().stream()
                .filter(pickChild -> CalculationUtil.compareTo(pickChild.getNeedNum(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该补料需求单的商品已经全部生成了补料出库单，那说明已经完成
            if (CollectionUtil.isEmpty(pickChildList)) {
                patchMaterialService.editOtherState(patchMaterial.getId(), OutLetState.COMPLATE_OUTLET.getKey());
            } else {
                patchMaterialService.editOtherState(patchMaterial.getId(), OutLetState.PARTIAL_OUTLET.getKey());
            }
        }
    }

    @Override
    public void approvalEndIsSuccess(PatchOutLet entity) {
        PatchOutLet oldEntity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(oldEntity, true);
    }

    @Override
    public void queryPatchOutLetTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PatchOutLet patchOutLet = selectById(id);
        // 该补料出库单下的已经下达仓库出库单(审核通过)的数量
        Map<String, String> depotNumMap = depotOutService.calcMaterialNormsNumByFromId(patchOutLet.getId());
        // 设置未下达商品数量-----补料出库单数量 - 已出库数量
        super.setOrCheckOperNumber(patchOutLet.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        patchOutLet.setErpOrderItemList(patchOutLet.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(patchOutLet);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertPatchOutLetToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotOut depotOut = inputObject.getParams(DepotOut.class);
        // 获取补料出库单状态
        PatchOutLet patchOutLet = selectById(depotOut.getId());
        if (ObjectUtil.isEmpty(patchOutLet)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(patchOutLet.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotOut.setFromId(depotOut.getId());
            depotOut.setFromTypeId(DepotOutFromType.PATCH_OUTLET.getKey());
            depotOut.setId(StrUtil.EMPTY);
            depotOutService.createEntity(depotOut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库出库单.");
        }
    }
}
