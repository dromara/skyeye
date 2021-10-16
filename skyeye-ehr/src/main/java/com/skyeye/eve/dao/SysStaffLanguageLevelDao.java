/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysStaffLanguageLevelDao {

	public List<Map<String, Object>> querySysStaffLanguageLevelList(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySysStaffLanguageLevelByName(Map<String, Object> params) throws Exception;

    public int insertSysStaffLanguageLevelMation(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySysStaffLanguageLevelById(@Param("id") String id) throws Exception;

    public Map<String, Object> querySysStaffLanguageLevelByNameAndId(Map<String, Object> params) throws Exception;

    public int editSysStaffLanguageLevelMationById(Map<String, Object> params) throws Exception;

    public Map<String, Object> querySysStaffLanguageLevelStateById(String id) throws Exception;

    public void editSysStaffLanguageLevelStateById(@Param("id") String id, @Param("state") int state) throws Exception;

    public List<Map<String, Object>> querySysStaffLanguageLevelUpMation(Map<String, Object> params) throws Exception;
}
