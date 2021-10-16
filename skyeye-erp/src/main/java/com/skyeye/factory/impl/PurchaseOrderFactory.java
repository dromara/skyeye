/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.factory.impl;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.PurchaseOrderDao;
import com.skyeye.factory.ErpOrderFactory;
import com.skyeye.factory.ErpOrderFactoryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PurchasePutFactory
 * @Description: 采购订单工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/9 22:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class PurchaseOrderFactory extends ErpOrderFactory implements ErpOrderFactoryResult {

    private PurchaseOrderDao purchaseOrderDao;

    public PurchaseOrderFactory(InputObject inputObject, OutputObject outputObject, String orderType) {
        super(inputObject, outputObject, orderType);
        this.purchaseOrderDao = SpringUtils.getBean(PurchaseOrderDao.class);
    }

    /**
     * 获取采购订单列表的执行sql
     *
     * @param params 入参
     * @return 订单列表
     */
    @Override
    protected List<Map<String, Object>> queryOrderListSqlRun(Map<String, Object> params) throws Exception {
        return purchaseOrderDao.queryPurchaseOrderToList(params);
    }

    /**
     * 新增订单时操作其他数据
     *
     * @param inputParams 前台传递的参数
     * @param orderId     订单id
     * @throws Exception
     */
    @Override
    protected void insertOrderOtherMation(Map<String, Object> inputParams, String orderId) throws Exception {
        // 生产计划单信息绑定
        String productionId = inputParams.get("productionId").toString();
        if(!ToolUtil.isBlank(productionId)){
            Map<String, Object> production = new HashMap<>();
            production.put("productionId", productionId);
            production.put("purchaseOrderId", orderId);
            erpCommonDao.insertProductionOrderRelationMation(production);
        }
    }

    /**
     * 编辑订单数据时，进行其他操作
     *
     * @param inputParams 前台传递的入参
     * @param orderId 订单id
     * @throws Exception
     */
    protected void editOrderOtherMationById(Map<String, Object> inputParams, String orderId) throws Exception{
        // 生产计划单信息绑定
        String productionId = inputParams.get("productionId").toString();
        if(!ToolUtil.isBlank(productionId)){
            Map<String, Object> production = new HashMap<>();
            production.put("productionId", productionId);
            production.put("purchaseOrderId", orderId);
            // 删除与生产计划单的关系
            erpCommonDao.deleteProductionOrderRelationMationByPurchaseOrderId(orderId);
            erpCommonDao.insertProductionOrderRelationMation(production);
        }
    }

    /**
     * 删除订单数据,操作其他数据
     *
     * @param orderMation 订单信息
     * @throws Exception
     */
    @Override
    protected void deleteOrderOtherMationById(Map<String, Object> orderMation) throws Exception{
        String orderId = orderMation.get("id").toString();
        // 删除与生产计划单的关系表
        erpCommonDao.deleteProductionOrderRelationMationByPurchaseOrderId(orderId);
    }

}
