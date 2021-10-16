/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PayMentDao
 * @Description: 付款单管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 22:17
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PayMentDao {
	
    public List<Map<String, Object>> queryPayMentByList(Map<String, Object> params) throws Exception;

    public int insertPayMent(Map<String, Object> params) throws Exception;

    public int insertPayMentItem(List<Map<String, Object>> entitys) throws Exception;

    public Map<String, Object> queryPayMentToEditById(Map<String, Object> params) throws Exception;

    public int editPayMentById(Map<String, Object> params) throws Exception;

    public int editPayMentByDeleteFlag(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryPayMentDetailById(Map<String, Object> params) throws Exception;

    public List<Map<String, Object>> queryPayMentItemsDetailById(Map<String, Object> bean) throws Exception;

    public List<Map<String, Object>> queryPayMentItemsToEditById(Map<String, Object> params) throws Exception;

    public int editPayMentItemsByDeleteFlag(Map<String, Object> params) throws Exception;

    public int deletePayMentItemById(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMationToExcel(Map<String, Object> params) throws Exception;
	
}
