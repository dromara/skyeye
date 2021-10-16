/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ErpOrderSaleOverPlanDao
 * @Description: ERP销售订单统筹管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/8 10:15
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ErpOrderSaleOverPlanDao {

    public void insertOrderSaleOverPlanMation(Map<String, Object> mation) throws Exception;

    public void deleteOrderSaleOverPlanMation(@Param("orderId") String orderId) throws Exception;

    public List<Map<String, Object>> queryOrderSaleOverPlanList(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryOrderSaleOverPlanMation(@Param("orderId") String orderId) throws Exception;

    public void editOrderSaleOverPlanMation(Map<String, Object> params) throws Exception;
}
