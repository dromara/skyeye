/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.factory.impl;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.RetailOutLetDao;
import com.skyeye.factory.ErpOrderFactory;
import com.skyeye.factory.ErpOrderFactoryResult;
import com.skyeye.service.StoreHouseApprovalService;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: RetailOutLetFactory
 * @Description: 零售出库单工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/16 20:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class RetailOutLetFactory extends ErpOrderFactory implements ErpOrderFactoryResult {

    private RetailOutLetDao retailOutLetDao;

    private StoreHouseApprovalService storeHouseApprovalService;

    public RetailOutLetFactory(InputObject inputObject, OutputObject outputObject, String orderType) {
        super(inputObject, outputObject, orderType);
        this.retailOutLetDao = SpringUtils.getBean(RetailOutLetDao.class);
        this.storeHouseApprovalService = SpringUtils.getBean(StoreHouseApprovalService.class);
    }

    /**
     * 获取订单列表的执行sql
     *
     * @param params 入参
     * @return 订单列表
     */
    @Override
    protected List<Map<String, Object>> queryOrderListSqlRun(Map<String, Object> params) throws Exception {
        return retailOutLetDao.queryRetailOutLetToList(params);
    }

    @Override
    protected void subOrderMationSuccessAfter(String orderId, String approvalResult) throws Exception{
        storeHouseApprovalService.approvalOrder(orderId, approvalResult);
    }

}
