/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WagesSocialSecurityFundApplicableObjectsDao {

    public int insertWagesSocialSecurityFundApplicableObjects(@Param("list") List<Map<String, Object>> beans) throws Exception;

    public int deleteWagesSocialSecurityFundApplicableObjectsBySecurityFundId(@Param("securityFundId") String securityFundId) throws Exception;

    public List<Map<String, Object>> queryWagesSocialSecurityFundApplicableObjectsBySecurityFundId(@Param("securityFundId") String securityFundId) throws Exception;

    public List<Map<String, Object>> queryAllWagesSocialSecurityFundApplicableObjects(@Param("lastMonthDate") String lastMonthDate) throws Exception;

}