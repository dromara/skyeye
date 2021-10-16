/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface CrmCustomerGroupDao {

    public int insertCustomerGroup(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryCustomerGroupList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCustomerGroupMationById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryStateById(Map<String, Object> map) throws  Exception;

    public List<Map<String, Object>> queryStateUpList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCustomerGroupByName(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryCustomerGroupByIdAndName(Map<String, Object> map) throws Exception;

    public int editCustomerGroupById(Map<String, Object> map) throws Exception;

    public int editStateUpById(Map<String, Object> map) throws Exception;

    public int editStateDownById(Map<String, Object> map) throws Exception;

    public int deleteCustomerGroupById(Map<String, Object> map) throws Exception;

}
