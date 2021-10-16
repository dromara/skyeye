/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 * @Author: 卫志强
 * @Description: TODO
 * @Date: 2019/9/16 21:23
 */
public interface SupplierDao {
    public List<Map<String, Object>> querySupplierByList(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySupplierByUserIdAndSupplier(Map<String, Object> params) throws Exception;

    public void insertSupplier(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySupplierById(Map<String, Object> params) throws Exception;

    public int editSupplierByDeleteFlag(Map<String, Object> params) throws Exception;

    public int editSupplierById(Map<String, Object> params) throws Exception;

    public int editSupplierByEnabled(Map<String, Object> params) throws Exception;

    public int editSupplierByNotEnabled(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySupplierByIdAndName(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySupplierByEnabled(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySupplierByIdAndInfo(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> querySupplierListTableToSelect(Map<String, Object> params) throws Exception;

}
