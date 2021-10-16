/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * ERP销售订单统筹
 *
 * @Author: 卫志强
 * @Date: 2021/02/27 15:43
 */
public interface ErpOrderSaleOverPlanService {

    /**
     * 新增销售订单统筹信息
     *
     * @param orderId 销售订单id
     * @throws Exception
     */
    public void insertOrderSaleOverPlanMation(String orderId) throws Exception;

    /**
     * 删除订单统筹信息
     *
     * @param orderId 销售订单id
     * @throws Exception
     */
    public void deleteOrderSaleOverPlanMation(String orderId) throws Exception;

    public void queryOrderSaleOverPlanList(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void editOrderSaleOverPlanMation(InputObject inputObject, OutputObject outputObject) throws Exception;

    public void queryOrderSaleOverPlanMationByOrderId(InputObject inputObject, OutputObject outputObject) throws Exception;
}
