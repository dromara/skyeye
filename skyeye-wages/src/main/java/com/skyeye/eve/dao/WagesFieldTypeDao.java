/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WagesFieldTypeDao {

    public List<Map<String, Object>> queryWagesFieldTypeList(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryWagesFieldTypeListByKey(@Param("key") String key) throws Exception;

    public int insertWagesFieldTypeMation(Map<String, Object> key) throws Exception;

    public int insertWagesFieldTypeKeyToStaff(@Param("list") List<Map<String, Object>> staff) throws Exception;

    public Map<String, Object> queryWagesFieldTypeMationById(@Param("id") String id) throws Exception;

    public int editWagesFieldTypeMationById(Map<String, Object> map) throws Exception;

    public int editWagesFieldTypeStateMationById(@Param("id") String id, @Param("state") int state) throws Exception;

    public List<Map<String, Object>> queryEnableWagesFieldTypeList(Map<String, Object> map) throws Exception;

    public List<Map<String, Object>> queryAllStaffMationList() throws Exception;

    /**
     * 获取所有的薪资要素字段，包括启用，禁用，以及删除的要素字段key；相同的key，该SQL语句会根据key进行分组
     *
     * @return List<Map<String, Object>>
     * @throws Exception
     */
    public List<Map<String, Object>> queryAllWagesFieldTypeList() throws Exception;
}
