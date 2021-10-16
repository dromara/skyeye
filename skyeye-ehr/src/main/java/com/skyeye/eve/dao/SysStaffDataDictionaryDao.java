/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysStaffDataDictionaryDao {

	public List<Map<String, Object>> querySysStaffDataDictionaryList(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySysStaffDataDictionaryByName(Map<String, Object> params) throws Exception;

    public int insertSysStaffDataDictionaryMation(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySysStaffDataDictionaryById(@Param("id") String id) throws Exception;

    public Map<String, Object> querySysStaffDataDictionaryByNameAndId(Map<String, Object> params) throws Exception;

    public int editSysStaffDataDictionaryMationById(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySysStaffDataDictionaryStateById(String id) throws Exception;

    public void editSysStaffDataDictionaryStateById(@Param("id") String id, @Param("state") int state) throws Exception;

    public List<Map<String, Object>> querySysStaffDataDictionaryUpMation(Map<String, Object> params) throws Exception;
}
