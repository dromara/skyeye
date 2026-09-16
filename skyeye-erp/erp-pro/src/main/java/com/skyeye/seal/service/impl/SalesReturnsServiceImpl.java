/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.seal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
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
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.seal.classenum.SealReturnFromType;
import com.skyeye.seal.dao.SalesReturnsDao;
import com.skyeye.seal.entity.SalesOrder;
import com.skyeye.seal.entity.SalesReturns;
import com.skyeye.seal.service.SalesOrderService;
import com.skyeye.seal.service.SalesOutLetService;
import com.skyeye.seal.service.SalesReturnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SalesReturnsServiceImpl
 * @Description: 销售退货单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "销售退货单", groupName = "销售模块", flowable = true, orderApproval = true)
public class SalesReturnsServiceImpl extends SkyeyeErpOrderServiceImpl<SalesReturnsDao, SalesReturns> implements SalesReturnsService {

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private SalesOutLetService salesOutLetService;

    @Autowired
    private DepotPutService depotPutService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置销售订单
        salesOrderService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(SalesReturns entity) {
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
    public void createPrepose(SalesReturns entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public SalesReturns selectById(String id) {
        SalesReturns salesOutLet = super.selectById(id);
        if (salesOutLet.getFromTypeId() == SealReturnFromType.SEAL_ORDER.getKey()) {
            // 销售订单
            salesOrderService.setDataMation(salesOutLet, SalesReturns::getFromId);
        }
        return salesOutLet;
    }

    private void checkMaterialNorms(SalesReturns entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前销售退货单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, 
                item -> StrUtil.isEmpty(item.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : item.getOperNumber()));
        // 获取已经下达销售退货单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == SealReturnFromType.SEAL_ORDER.getKey()) {
            // 销售订单
            checkAndUpdateSalesOrderState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateSalesOrderState(SalesReturns entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        SalesOrder salesOrder = salesOrderService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(salesOrder.getErpOrderItemList())) {
            throw new CustomException("该销售订单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(salesOrder.getErpOrderItemList(), inSqlNormsId);
        // 获取已经下达销售出库单的商品信息
        Map<String, String> returnExecuteNum = salesOutLetService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经出库的商品数量 - 已经退货的商品数量
        super.setOrCheckOperNumber(salesOrder.getErpOrderItemList(), setData, orderNormsNum, executeNum, returnExecuteNum);
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
    public void approvalEndIsSuccess(SalesReturns entity) {
        entity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(entity, true);
        // 减少已分配量库存
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpCommonService.editMaterialNormsDepotStock(MaterialNormsStockType.ALLOCATED_STOCK.getDefaultDepotId(), erpOrderItem.getMaterialId(),
                erpOrderItem.getNormsId(), erpOrderItem.getOperNumber(), DepotPutOutType.OUT.getKey(), MaterialNormsStockType.ALLOCATED_STOCK.getKey());
        });
    }

    @Override
    public void querySalesReturnsTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        SalesReturns salesReturns = selectById(id);
        if (salesReturns.getNeedDepot() == WhetherEnum.DISABLE_USING.getKey()) {
            throw new CustomException("该销售退货单无需进行转入库操作");
        }
        // 该销售退货单下的已经下达仓库入库单(审核通过)的数量
        Map<String, String> depotNumMap = depotPutService.calcMaterialNormsNumByFromId(salesReturns.getId());
        // 设置未下达商品数量-----销售退货单数量 - 已入库数量
        super.setOrCheckOperNumber(salesReturns.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        salesReturns.setErpOrderItemList(salesReturns.getErpOrderItemList().stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber()) 
                    ? CommonNumConstants.NUM_ZERO.toString() 
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(salesReturns);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertSalesReturnsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        // 获取销售退货单状态
        SalesReturns salesReturns = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(salesReturns)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库入库单
        if (FlowableStateEnum.PASS.getKey().equals(salesReturns.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.SEAL_RETURNS.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }
}
