/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.seal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.constants.FlowableConstants;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.seal.classenum.SalesExchangesFromType;
import com.skyeye.seal.classenum.SealOutLetFromType;
import com.skyeye.seal.classenum.SealReturnFromType;
import com.skyeye.seal.dao.SalesExchangesDao;
import com.skyeye.seal.entity.SalesExchanges;
import com.skyeye.seal.entity.SalesOrder;
import com.skyeye.seal.entity.SalesOutLet;
import com.skyeye.seal.service.SalesExchangesService;
import com.skyeye.seal.service.SalesOrderService;
import com.skyeye.seal.service.SalesOutLetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "销售换货单", groupName = "销售模块", flowable = true, orderApproval = true)
public class SalesExchangesServiceImpl extends SkyeyeErpOrderServiceImpl<SalesExchangesDao, SalesExchanges> implements SalesExchangesService {

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private SalesOutLetService salesOutLetService;

    @Autowired
    private DepotPutService depotPutService;

    @Autowired
    private SalesExchangesService salesExchangesService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置销售订单
        salesOrderService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(SalesExchanges entity) {
        if (entity.getNeedDepot() == WhetherEnum.ENABLE_USING.getKey()) {
            entity.setOtherState(DepotPutState.NEED_PUT.getKey());
            entity.getErpOrderItemList().forEach(erpOrderItem -> {
                if (StrUtil.isEmpty(erpOrderItem.getDepotId())) {
                    throw new CustomException("请选择入库仓库.");
                }
            });
        } else {
            entity.setOtherState(DepotPutState.NOT_NEED_PUT.getKey());
        }
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(SalesExchanges entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public SalesExchanges selectById(String id) {
        SalesExchanges salesExchanges = super.selectById(id);
        if (salesExchanges.getFromTypeId() == SealReturnFromType.SEAL_ORDER.getKey()) {
            // 销售订单
            salesOrderService.setDataMation(salesExchanges, SalesExchanges::getFromId);
        }
        return salesExchanges;
    }

    private void checkMaterialNorms(SalesExchanges entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前销售换货单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
                .collect(Collectors.toMap(ErpOrderItem::getNormsId, 
                    item -> StrUtil.isEmpty(item.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : item.getOperNumber()));
        // 获取已经下达销售换货单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == SalesExchangesFromType.SEAL_ORDER.getKey()) {
            // 销售订单
            checkAndUpdateSalesOrderState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateSalesOrderState(SalesExchanges entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        SalesOrder salesOrder = salesOrderService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(salesOrder.getErpOrderItemList())) {
            throw new CustomException("该销售订单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(salesOrder.getErpOrderItemList(), inSqlNormsId);
        // 获取已经下达销售出库单的商品信息
        Map<String, String> returnExecuteNum = salesOutLetService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 获取已经下达销售换货单的商品信息
        Map<String, String> returnExchangesNum = salesExchangesService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经出库的商品数量 - 已经退货的商品数量 - 已经换货的商品数量
        super.setOrCheckOperNumber(salesOrder.getErpOrderItemList(), setData, orderNormsNum, executeNum, returnExecuteNum, returnExchangesNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = salesOrder.getErpOrderItemList().stream()
                    .filter(erpOrderItem -> {
                        String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber()) 
                            ? CommonNumConstants.NUM_ZERO.toString() 
                            : erpOrderItem.getOperNumber();
                        return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
                    }).collect(Collectors.toList());
            // 如果该销售订单的商品已经全部生成了销售出库单/销售退货单，那说明已经完成了销售订单的内容
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                salesOrderService.editStateById(salesOrder.getId(), ErpOrderStateEnum.COMPLETED.getKey());
            } else {
                salesOrderService.editStateById(salesOrder.getId(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey());
            }
        }
    }

    @Override
    public void approvalEnd(String processInstanceId, String result) {
        SalesExchanges entity = selectByProcessInstanceId(processInstanceId);
        if (FlowableConstants.APPROVAL_PASS.equalsIgnoreCase(result)) {
            approvalEndIsSuccess(entity);
            // 换货单审批通过后修改状态为待出库
            editStateById(entity.getId(), ErpOrderStateEnum.NEED_Out.getKey());
        } else {
            approvalEndIsFailed(entity);
            editStateById(entity.getId(), FlowableStateEnum.REJECT.getKey());
        }
    }

    @Override
    public void approvalEndIsSuccess(SalesExchanges entity) {
        entity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(entity, true);
    }

    @Override
    public void querySalesExchangesToDepotPutById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        SalesExchanges salesExchanges = selectById(id);
        if (salesExchanges.getNeedDepot() == WhetherEnum.DISABLE_USING.getKey()) {
            throw new CustomException("该销售退货单无需进行转入库操作");
        }
        // 该销售换货单下的已经下达仓库入库单(审核通过)的数量
        Map<String, String> depotNumMap = depotPutService.calcMaterialNormsNumByFromId(salesExchanges.getId());
        // 设置未下达商品数量-----销售换货单数量 - 已入库数量
        super.setOrCheckOperNumber(salesExchanges.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        salesExchanges.setErpOrderItemList(salesExchanges.getErpOrderItemList().stream()
                .filter(erpOrderItem -> {
                    String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber()) 
                        ? CommonNumConstants.NUM_ZERO.toString() 
                        : erpOrderItem.getOperNumber();
                    return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
                }).collect(Collectors.toList()));
        outputObject.setBean(salesExchanges);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertSalesExchangesToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        // 获取销售退货单状态
        SalesExchanges salesExchanges = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(salesExchanges)) {
            throw new CustomException("该数据不存在.");
        }
        // 当状态为待出库、部分出库、全部出库时，表示已经审核通过     并且该单据需要入库时，则可以进行转入库
        if (Arrays.asList(ErpOrderStateEnum.NEED_Out.getKey(), ErpOrderStateEnum.PARTIAL_Out.getKey(), ErpOrderStateEnum.All_Out.getKey())
                .contains(salesExchanges.getState()) && Objects.equals(salesExchanges.getNeedDepot(), WhetherEnum.ENABLE_USING.getKey())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.SALES_EXCHANGES.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }

    @Override
    public void querySalesExchangesToSalesOutLetById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        SalesExchanges salesExchanges = selectById(id);
        if (salesExchanges.getNeedDepot() == WhetherEnum.DISABLE_USING.getKey()) {
            throw new CustomException("该销售退货单无需进行转入库操作");
        }
        // 该销售换货单下的已经下达销售出库单(审核通过)的数量
        Map<String, String> depotNumMap = salesOutLetService.calcMaterialNormsNumByFromId(salesExchanges.getId());
        // 设置未下达商品数量-----销售换货单数量 - 已出库数量
        super.setOrCheckOperNumber(salesExchanges.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        salesExchanges.setErpOrderItemList(salesExchanges.getErpOrderItemList().stream()
                .filter(erpOrderItem -> {
                    String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber()) 
                        ? CommonNumConstants.NUM_ZERO.toString() 
                        : erpOrderItem.getOperNumber();
                    return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
                }).collect(Collectors.toList()));
        outputObject.setBean(salesExchanges);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertSalesExchangesToSalesOutLet(InputObject inputObject, OutputObject outputObject) {
        DepotPut salesOutLet = inputObject.getParams(DepotPut.class);
        // 获取销售退货单状态
        SalesExchanges salesExchanges = selectById(salesOutLet.getId());
        if (ObjectUtil.isEmpty(salesExchanges)) {
            throw new CustomException("该数据不存在.");
        }
        // 当状态为待出库、部分出库、全部出库时，表示已经审核通过   此时可以进行出库操作
        if (Arrays.asList(ErpOrderStateEnum.NEED_Out.getKey(), ErpOrderStateEnum.PARTIAL_Out.getKey(), ErpOrderStateEnum.All_Out.getKey())
                .contains(salesExchanges.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            salesOutLet.setFromId(salesOutLet.getId());
            salesOutLet.setFromTypeId(SealOutLetFromType.SALE_EXCHANGES.getKey());
            salesOutLet.setId(StrUtil.EMPTY);
            // 使用 JSON 序列化和反序列化进行转换
            SalesOutLet salesOutLetConverted = JSONUtil.toBean(JSONUtil.toJsonStr(salesOutLet), SalesOutLet.class);
            salesOutLetService.createEntity(salesOutLetConverted, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }
}
