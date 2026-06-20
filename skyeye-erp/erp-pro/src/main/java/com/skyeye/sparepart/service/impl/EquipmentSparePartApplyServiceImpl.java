/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.business.service.SkyeyeErpOrderItemService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.otherwise.entity.OtherWiseOrder;
import com.skyeye.otherwise.service.OtherWiseOrderService;
import com.skyeye.sparepart.classenum.EquipmentUserStockPutOutType;
import com.skyeye.sparepart.dao.EquipmentSparePartApplyDao;
import com.skyeye.sparepart.entity.EquipmentSparePartApply;
import com.skyeye.sparepart.entity.EquipmentSparePartApplyChangeStock;
import com.skyeye.sparepart.entity.EquipmentSparePartApplyLink;
import com.skyeye.sparepart.service.EquipmentSparePartApplyLinkService;
import com.skyeye.sparepart.service.EquipmentSparePartApplyService;
import com.skyeye.sparepart.service.EquipmentUserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备备件-申领单
 */
@Service
@SkyeyeService(name = "备件申领单", groupName = "设备备件", flowable = true)
public class EquipmentSparePartApplyServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentSparePartApplyDao, EquipmentSparePartApply>
    implements EquipmentSparePartApplyService {

    @Autowired
    private EquipmentSparePartApplyLinkService equipmentSparePartApplyLinkService;

    @Autowired
    private EquipmentUserStockService equipmentUserStockService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private OtherWiseOrderService otherWiseOrderService;

    @Autowired
    private SkyeyeErpOrderItemService skyeyeErpOrderItemService;

    @Override
    public QueryWrapper<EquipmentSparePartApply> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentSparePartApply> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentSparePartApply::getCreateId), userId);
        return queryWrapper;
    }

    @Override
    public void validatorEntity(EquipmentSparePartApply entity) {
        if (CollectionUtil.isEmpty(entity.getApplyLinkList())) {
            throw new CustomException("请至少填写一条申领明细");
        }
        String allPrice = equipmentSparePartApplyLinkService.calcOrderAllTotalPrice(entity.getApplyLinkList());
        entity.setAllPrice(allPrice);
        entity.setOtherState(CommonNumConstants.NUM_TWO);
    }

    @Override
    public void writePostpose(EquipmentSparePartApply entity, String userId) {
        equipmentSparePartApplyLinkService.saveLinkList(entity.getId(), entity.getApplyLinkList());
        super.writePostpose(entity, userId);
    }

    @Override
    protected void deletePreExecution(EquipmentSparePartApply entity) {
        if (!checkState(entity)) {
            throw new CustomException("该数据状态已改变，删除失败.");
        }
        equipmentSparePartApplyLinkService.deleteByPId(entity.getId());
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        equipmentSparePartApplyLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public EquipmentSparePartApply getDataFromDb(String id) {
        EquipmentSparePartApply apply = super.getDataFromDb(id);
        apply.setApplyLinkList(equipmentSparePartApplyLinkService.selectByPId(apply.getId()));
        return apply;
    }

    @Override
    public EquipmentSparePartApply selectById(String id) {
        EquipmentSparePartApply apply = super.selectById(id);
        if (apply == null || CollectionUtil.isEmpty(apply.getApplyLinkList())) {
            return apply;
        }
        materialService.setDataMation(apply.getApplyLinkList(), EquipmentSparePartApplyLink::getMaterialId);
        materialNormsService.setDataMation(apply.getApplyLinkList(), EquipmentSparePartApplyLink::getNormsId);
        erpDepotService.setDataMation(apply.getApplyLinkList(), EquipmentSparePartApplyLink::getDepotId);
        return apply;
    }

    @Override
    public void revokePostpose(EquipmentSparePartApply entity) {
        super.revokePostpose(entity);
        equipmentSparePartApplyLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void approvalEndIsSuccess(EquipmentSparePartApply entity) {
        EquipmentSparePartApply apply = selectById(entity.getId());
        equipmentSparePartApplyLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
        saveOrderToErp(apply);
    }

    @Override
    public void approvalEndIsFailed(EquipmentSparePartApply entity) {
        equipmentSparePartApplyLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    @Override
    public void editApplyOtherState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        editApplyOtherState(params.get("id").toString(), Integer.parseInt(params.get("otherState").toString()));
    }

    @Override
    public void editApplyOtherState(String id, Integer otherState) {
        UpdateWrapper<EquipmentSparePartApply> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentSparePartApply::getOtherState), otherState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void editApplyOutNum(InputObject inputObject, OutputObject outputObject) {
        editApplyOutNum(inputObject.getParams(EquipmentSparePartApplyChangeStock.class));
    }

    @Override
    public void editApplyOutNum(EquipmentSparePartApplyChangeStock changeStock) {
        if (CollectionUtil.isEmpty(changeStock.getApplyLinkList())) {
            return;
        }
        changeStock.getApplyLinkList().forEach(applyLink ->
            equipmentUserStockService.editMaterialNormsUserStock(changeStock.getCreateId(), applyLink.getMaterialId(),
                applyLink.getNormsId(), applyLink.getOperNumber(), EquipmentUserStockPutOutType.PUT.getKey()));
    }

    private void saveOrderToErp(EquipmentSparePartApply apply) {
        OtherWiseOrder order = new OtherWiseOrder();
        order.setId(apply.getId());
        order.setOddNumber(apply.getOddNumber());
        order.setState(FlowableStateEnum.PASS.getKey());
        order.setIdKey(getServiceClassName());
        order.setOperTime(apply.getApplyTime());
        order.setTotalPrice(apply.getAllPrice());
        order.setDiscount(CommonNumConstants.NUM_ZERO.toString());
        order.setDiscountMoney(CommonNumConstants.NUM_ZERO.toString());
        order.setCreateId(apply.getCreateId());
        List<ErpOrderItem> erpOrderItemList = new ArrayList<>();
        apply.getApplyLinkList().forEach(applyLink -> {
            ErpOrderItem erpOrderItem = BeanUtil.copyProperties(applyLink, ErpOrderItem.class);
            erpOrderItem.setTaxMoney(CommonNumConstants.NUM_ZERO.toString());
            erpOrderItem.setTaxUnitPrice(CommonNumConstants.NUM_ZERO.toString());
            erpOrderItem.setTaxLastMoney(CommonNumConstants.NUM_ZERO.toString());
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
            erpOrderItem.setState(FlowableChildStateEnum.ADEQUATE.getKey());
            erpOrderItemList.add(erpOrderItem);
        });
        otherWiseOrderService.save(order);
        skyeyeErpOrderItemService.saveLinkList(order.getId(), erpOrderItemList);
    }

}
