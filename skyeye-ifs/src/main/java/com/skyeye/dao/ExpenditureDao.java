/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 * @Author: 卫志强
 * @Description: TODO
 * @Date: 2019/10/20 10:23
 */
public interface ExpenditureDao {
	
    public List<Map<String, Object>> queryExpenditureByList(Map<String, Object> params) throws Exception;

    public int insertExpenditure(Map<String, Object> params) throws Exception;

    public int insertExpenditureItem(List<Map<String, Object>> entitys) throws Exception;

    public Map<String, Object> queryExpenditureToEditById(Map<String, Object> params) throws Exception;

    public int editExpenditureById(Map<String, Object> params) throws Exception;

    public int editExpenditureByDeleteFlag(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryExpenditureDetailById(Map<String, Object> params) throws Exception;

    public List<Map<String, Object>> queryExpenditureItemsDetailById(Map<String, Object> bean) throws Exception;

    public List<Map<String, Object>> queryExpenditureItemsToEditById(Map<String, Object> params) throws Exception;

    public int editExpenditureItemsByDeleteFlag(Map<String, Object> params) throws Exception;

    public int deleteExpenditureItemById(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMationToExcel(Map<String, Object> params) throws Exception;
	
}
