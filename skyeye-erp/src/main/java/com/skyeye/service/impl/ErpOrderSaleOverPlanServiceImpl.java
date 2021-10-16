/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.dao.ErpOrderSaleOverPlanDao;
import com.skyeye.service.ErpOrderSaleOverPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ErpOrderSaleOverPlanServiceImpl
 * @Description: ERP销售订单统筹服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:42
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpOrderSaleOverPlanServiceImpl implements ErpOrderSaleOverPlanService {

    @Autowired
    private ErpOrderSaleOverPlanDao erpOrderSaleOverPlanDao;

    public static enum State{
        NON_OVER_PLAN(1, "未统筹"),
        ALREADY_OVER_PLAN(2, "已统筹");

        private int state;
        private String name;

        State(int state, String name){
            this.state = state;
            this.name = name;
        }

        public int getState() {
            return state;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 新增销售订单统筹信息
     *
     * @param orderId 销售订单id
     * @throws Exception
     */
    @Override
    public void insertOrderSaleOverPlanMation(String orderId) throws Exception{
        Map<String, Object> mation = new HashMap<>();
        mation.put("orderId", orderId);
        mation.put("state", State.NON_OVER_PLAN.getState());
        erpOrderSaleOverPlanDao.insertOrderSaleOverPlanMation(mation);
    }

    /**
     * 删除订单统筹信息
     *
     * @param orderId 销售订单id
     * @throws Exception
     */
    @Override
    public void deleteOrderSaleOverPlanMation(String orderId) throws Exception {
        erpOrderSaleOverPlanDao.deleteOrderSaleOverPlanMation(orderId);
    }

    /**
     * 销售订单统筹信息列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryOrderSaleOverPlanList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = erpOrderSaleOverPlanDao.queryOrderSaleOverPlanList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 销售订单统筹
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void editOrderSaleOverPlanMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String orderId = params.get("id").toString();
        Map<String, Object> mation = erpOrderSaleOverPlanDao.queryOrderSaleOverPlanMation(orderId);
        if(mation != null && !mation.isEmpty()){
            int nowState = Integer.parseInt(mation.get("state").toString());
            if(nowState == State.NON_OVER_PLAN.getState()){
                params.put("overPlanId", inputObject.getLogParams().get("id"));
                params.put("overPlanTime", DateUtil.getTimeAndToString());
                params.put("state", State.ALREADY_OVER_PLAN.getState());
                erpOrderSaleOverPlanDao.editOrderSaleOverPlanMation(params);
            }
        }else{
            outputObject.setreturnMessage("this sale over plan data is non-exist.");
        }
    }

    /**
     * 根据销售订单id获取统筹信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryOrderSaleOverPlanMationByOrderId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String orderId = params.get("id").toString();
        Map<String, Object> mation = erpOrderSaleOverPlanDao.queryOrderSaleOverPlanMation(orderId);
        if(mation != null && !mation.isEmpty()){
            outputObject.setBean(mation);
        }else{
            outputObject.setreturnMessage("this sale over plan data is non-exist.");
        }
    }

}
