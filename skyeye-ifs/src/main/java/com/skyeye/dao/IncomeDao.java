/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: IncomeDao
 * @Description: 记账收入管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 22:22
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IncomeDao {
    public List<Map<String, Object>> queryIncomeByList(Map<String, Object> params) throws Exception;

    public int insertIncome(Map<String, Object> params) throws Exception;

    public int insertIncomeItem(List<Map<String, Object>> entitys) throws Exception;

    public Map<String, Object> queryIncomeToEditById(Map<String, Object> params) throws Exception;

    public int editIncomeById(Map<String, Object> params) throws Exception;

    public int editIncomeByDeleteFlag(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryIncomeDetailById(Map<String, Object> params) throws Exception;

    public List<Map<String, Object>> queryIncomeItemsDetailById(Map<String, Object> bean) throws Exception;

    public List<Map<String, Object>> queryIncomeItemsToEditById(Map<String, Object> params) throws Exception;

    public int editIncomeItemsByDeleteFlag(Map<String, Object> params) throws Exception;

    public int deleteIncomeItemById(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMationToExcel(Map<String, Object> params) throws Exception;
}
