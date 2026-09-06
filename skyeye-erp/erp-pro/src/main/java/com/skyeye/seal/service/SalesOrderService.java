/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.seal.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.seal.entity.SalesOrder;

/**
 * @ClassName: SalesOrderService
 * @Description: 销售订单管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SalesOrderService extends SkyeyeErpOrderService<SalesOrder> {

    void querySalesOrderListToTree(InputObject inputObject, OutputObject outputObject);

    void querySalesOrderMaterialListByOrderId(InputObject inputObject, OutputObject outputObject);

    void querySealsOrderTransById(InputObject inputObject, OutputObject outputObject);

    void insertSalesOrderToTurnPut(InputObject inputObject, OutputObject outputObject);

    void queryCrmContractTransById(InputObject inputObject, OutputObject outputObject);

    void insertCrmContractToSealsOrder(InputObject inputObject, OutputObject outputObject);

    void insertSealsOrderToSealsReturns(InputObject inputObject, OutputObject outputObject);

    void querySealsOrderTransProductionPlanById(InputObject inputObject, OutputObject outputObject);

    void insertSealsOrderToProductionPlan(InputObject inputObject, OutputObject outputObject);

    void insertSealsOrderToSealExchanges(InputObject inputObject, OutputObject outputObject);

    void querySealsOrderTransPurchaseOrderById(InputObject inputObject, OutputObject outputObject);

    void insertSealsOrderToPurchaseOrder(InputObject inputObject, OutputObject outputObject);

    /**
     * 修改销售订单采购状态
     *
     * @param id            销售订单id
     * @param purchaseState 采购状态 {@link com.skyeye.seal.classenum.SalesOrderPurchaseState}
     */
    void editPurchaseState(String id, Integer purchaseState);
}
