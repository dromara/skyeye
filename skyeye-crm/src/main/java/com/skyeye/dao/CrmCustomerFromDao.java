/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface CrmCustomerFromDao {

    public int insertCrmCustomerFrom(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryCrmCustomerFromList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCrmCustomerFromMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryStateById(Map<String, Object> map) throws  Exception;

    public List<Map<String, Object>> queryStateUpList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCrmCustomerFromByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCrmCustomerFromByIdAndName(Map<String, Object> map) throws Exception;

    public int editCrmCustomerFromById(Map<String, Object> map) throws Exception;

    public int editStateUpById(Map<String, Object> map) throws Exception;

    public int editStateDownById(Map<String, Object> map) throws Exception;

    public int deleteCrmCustomerFromById(Map<String, Object> map) throws Exception;

}
