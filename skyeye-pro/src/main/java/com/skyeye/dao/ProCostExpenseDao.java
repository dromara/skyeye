/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProCostExpenseDao
 * @Description: 项目费用报销管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:40
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProCostExpenseDao {

	public List<Map<String, Object>> queryProCostExpenseList(Map<String, Object> map) throws Exception;

	public int insertProCostExpenseMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProCostExpenseMationById(@Param("id") String id) throws Exception;

	public Map<String, Object> queryProCostExpenseMationToEdit(Map<String, Object> map) throws Exception;

	public int editProCostExpenseMation(Map<String, Object> map) throws Exception;

	public int updateProCostExpenseStateISInAudit(@Param("id") String id) throws Exception;

	public int deleteProCostExpenseProcessById(@Param("id") String id) throws Exception;

	public int insertProCostExpenseProcess(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryExpenseIdByProcessInstanceId(@Param("processInstanceId") String processInstanceId) throws Exception;

	public int editProCostExpenseStateById(Map<String, Object> map) throws Exception;

	public int editProCostExpenseProcessStateById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProCostExpenseId(Map<String, Object> map) throws Exception;

	public int editProCostExpenseProcessToRevoke(Map<String, Object> map) throws Exception;

	public int deleteProCostExpenseProcessToRevoke(Map<String, Object> map) throws Exception;

	public int deleteProCostExpenseMationById(Map<String, Object> map) throws Exception;

	public int updateProCostExpenseToCancellation(Map<String, Object> map) throws Exception;

	public int insertProCostExpensePurposeMation(List<Map<String, Object>> entitys) throws Exception;

	public List<Map<String, Object>> queryProCostExpensePurpose(@Param("expenseId") String expenseId) throws Exception;

	public List<Map<String, Object>> queryProCostExpensePurposeToEdit(Map<String, Object> map) throws Exception;

	public int deleteProCostExpensePurposeById(Map<String, Object> map) throws Exception;

}
