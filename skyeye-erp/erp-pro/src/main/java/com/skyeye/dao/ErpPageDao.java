/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ErpPageDao
 * @Description: ERP统计模块数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/2 11:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ErpPageDao {

    @IgnoreTenant
    String queryThisMonthErpOrder(@Param("idKey") String idKey,
                                  @Param("states") List<String> states,
                                  @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> querySixMonthOrderMoneyList(@Param("idKey") String idKey,
                                                          @Param("states") List<String> states,
                                                          @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryTwelveMonthProfitMoneyList(@Param("idKeys") List<String> idKeys,
                                                              @Param("states") List<String> states,
                                                              @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryProcessFlowCount(@Param("params") Map<String, Object> params);

}
