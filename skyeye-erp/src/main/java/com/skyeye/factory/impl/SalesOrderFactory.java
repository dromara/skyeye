/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.factory.impl;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.dao.SalesOrderDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.factory.ErpOrderFactory;
import com.skyeye.factory.ErpOrderFactoryResult;
import com.skyeye.service.ErpOrderSaleOverPlanService;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SalesOrderFactory
 * @Description: 销售订单工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/14 23:02
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SalesOrderFactory extends ErpOrderFactory implements ErpOrderFactoryResult {

    private SalesOrderDao salesOrderDao;

    private ErpOrderSaleOverPlanService erpOrderSaleOverPlanService;

    private SysEveUserStaffDao sysEveUserStaffDao;

    public SalesOrderFactory(InputObject inputObject, OutputObject outputObject, String orderType) {
        super(inputObject, outputObject, orderType);
        this.salesOrderDao = SpringUtils.getBean(SalesOrderDao.class);
        this.erpOrderSaleOverPlanService = SpringUtils.getBean(ErpOrderSaleOverPlanService.class);
        this.sysEveUserStaffDao = SpringUtils.getBean(SysEveUserStaffDao.class);
    }

    /**
     * 获取订单列表的执行sql
     *
     * @param params 入参
     * @return 订单列表
     */
    @Override
    protected List<Map<String, Object>> queryOrderListSqlRun(Map<String, Object> params) throws Exception {
        return salesOrderDao.querySalesOrderToList(params);
    }

    /**
     * 新增订单时操作其他数据--处理销售订单统筹信息
     *
     * @param inputParams 前台传递的参数
     * @param orderId 订单id
     * @throws Exception
     */
    @Override
    protected void insertOrderOtherMation(Map<String, Object> inputParams, String orderId) throws Exception {
        // needOverPlan-是否需要统筹  1.需要  2.不需要
        String needOverPlan = inputParams.get("needOverPlan").toString();
        erpOrderSaleOverPlanService.deleteOrderSaleOverPlanMation(orderId);
        if("1".equals(needOverPlan)){
            // 需要新增统筹信息
            erpOrderSaleOverPlanService.insertOrderSaleOverPlanMation(orderId);
        }
    }

    /**
     * 删除订单数据,操作其他数据--删除统筹信息
     *
     * @param orderMation 订单信息
     * @throws Exception
     */
    protected void deleteOrderOtherMationById(Map<String, Object> orderMation) throws Exception{
        String orderId = orderMation.get("id").toString();
        // needOverPlan-是否需要统筹  1.需要  2.不需要
        String needOverPlan = orderMation.get("needOverPlan").toString();
        if("1".equals(needOverPlan)){
            // 删除统筹信息
            erpOrderSaleOverPlanService.deleteOrderSaleOverPlanMation(orderId);
        }
    }

    /**
     * 编辑时获取订单数据进行回显时，获取其他数据--获取销售人员
     *
     * @param bean 单据信息
     * @param orderId 订单id
     * @throws Exception
     */
    protected void quertOrderOtherMationToEditById(Map<String, Object> bean, String orderId) throws Exception{
        // 获取销售人员
        if(bean.containsKey("salesMan")){
            bean.put("userInfo", sysEveUserStaffDao.queryUserNameList(bean.get("salesMan").toString()));
        }
    }

    /**
     * 编辑订单数据时，进行其他操作--处理销售订单统筹信息
     *
     * @param inputParams 前台传递的参数
     * @param orderId 订单id
     * @throws Exception
     */
    @Override
    protected void editOrderOtherMationById(Map<String, Object> inputParams, String orderId) throws Exception {
        // needOverPlan-是否需要统筹  1.需要  2.不需要
        String needOverPlan = inputParams.get("needOverPlan").toString();
        erpOrderSaleOverPlanService.deleteOrderSaleOverPlanMation(orderId);
        if("1".equals(needOverPlan)){
            // 需要新增统筹信息
            erpOrderSaleOverPlanService.insertOrderSaleOverPlanMation(orderId);
        }
    }

}
