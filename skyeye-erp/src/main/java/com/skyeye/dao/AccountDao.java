/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 * @Author: 卫志强
 * @Date: 2019/10/6 15:43
 */
public interface AccountDao {
    public List<Map<String, Object>> queryAccountByList(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryAccountByName(Map<String, Object> params) throws Exception;

    public int insertAccount(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryAccountById(Map<String, Object> params) throws Exception;

    public int editAccountByDeleteFlag(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryAccountByIdAndName(Map<String, Object> params) throws Exception;

    public int editAccountById(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryAccountByIdAndIsDeafault(Map<String, Object> params) throws Exception;

    public int editAccountByIsNotDefault(Map<String, Object> params) throws Exception;

    public int editAccountByIdAndIsDefault(Map<String, Object> params) throws Exception;

    public Map<String, Object> queryAccountByIdAndInfo(Map<String, Object> params) throws Exception;

    public List<Map<String, Object>> queryAccountStreamById(Map<String, Object> params) throws Exception;

	public List<Map<String, Object>> queryAccountListToSelect(Map<String, Object> params) throws Exception;

}
