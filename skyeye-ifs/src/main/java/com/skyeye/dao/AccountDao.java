/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AccountDao
 * @Description: 财务账户数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/24 21:54
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
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
