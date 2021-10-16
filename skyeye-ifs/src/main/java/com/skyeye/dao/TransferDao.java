/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: TransferDao
 * @Description: 转账单管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 22:17
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TransferDao {
	
    public List<Map<String, Object>> queryTransferByList(Map<String, Object> params) throws Exception;

    public int insertTransfer(Map<String, Object> params) throws Exception;

    public int insertTransferItem(List<Map<String, Object>> entitys) throws Exception;

    public Map<String, Object> queryTransferToEditById(Map<String, Object> params) throws Exception;

    public int editTransferById(Map<String, Object> params) throws Exception;

    public int editTransferByDeleteFlag(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryTransferDetailById(Map<String, Object> params) throws Exception;

    public List<Map<String, Object>> queryTransferItemsDetailById(Map<String, Object> bean) throws Exception;

    public List<Map<String, Object>> queryTransferItemsToEditById(Map<String, Object> params) throws Exception;

    public int editTransferItemsByDeleteFlag(Map<String, Object> params) throws Exception;

    public int deleteTransferItemById(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryMationToExcel(Map<String, Object> params) throws Exception;
	
}
