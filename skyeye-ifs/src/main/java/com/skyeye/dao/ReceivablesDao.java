/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ReceivablesDao
 * @Description: 收款单管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 22:16
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ReceivablesDao {
	
    public List<Map<String, Object>> queryReceivablesByList(Map<String, Object> params) throws Exception;

    public int insertReceivables(Map<String, Object> params) throws Exception;

    public int insertReceivablesItem(List<Map<String, Object>> entitys) throws Exception;

    public Map<String, Object> queryReceivablesToEditById(Map<String, Object> params) throws Exception;

    public int editReceivablesById(Map<String, Object> params) throws Exception;

    public int editReceivablesByDeleteFlag(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryReceivablesDetailById(Map<String, Object> params) throws Exception;

    public List<Map<String, Object>> queryReceivablesItemsDetailById(Map<String, Object> bean) throws Exception;

    public List<Map<String, Object>> queryReceivablesItemsToEditById(Map<String, Object> params) throws Exception;

    public int editReceivablesItemsByDeleteFlag(Map<String, Object> params) throws Exception;

    public int deleteReceivablesItemById(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMationToExcel(Map<String, Object> params) throws Exception;
	
}
