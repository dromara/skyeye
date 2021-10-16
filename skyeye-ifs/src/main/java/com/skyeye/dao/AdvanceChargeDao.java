/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AdvanceChargeDao
 * @Description: 收预付款管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 22:18
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AdvanceChargeDao {
	
    public List<Map<String, Object>> queryAdvanceChargeByList(Map<String, Object> params) throws Exception;

    public int insertAdvanceCharge(Map<String, Object> params) throws Exception;

    public int insertAdvanceChargeItem(List<Map<String, Object>> entitys) throws Exception;

    public Map<String, Object> queryAdvanceChargeToEditById(Map<String, Object> params) throws Exception;

    public int editAdvanceChargeById(Map<String, Object> params) throws Exception;

    public int editAdvanceChargeByDeleteFlag(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryAdvanceChargeDetailById(Map<String, Object> params) throws Exception;

    public List<Map<String, Object>> queryAdvanceChargeItemsDetailById(Map<String, Object> bean) throws Exception;

    public List<Map<String, Object>> queryAdvanceChargeItemsToEditById(Map<String, Object> params) throws Exception;

    public int editAdvanceChargeItemsByDeleteFlag(Map<String, Object> params) throws Exception;

    public int deleteAdvanceChargeItemById(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMationToExcel(Map<String, Object> params) throws Exception;
	
}
