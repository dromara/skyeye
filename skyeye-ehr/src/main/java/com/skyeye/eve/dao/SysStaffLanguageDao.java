/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysStaffLanguageDao {

	public List<Map<String, Object>> queryAllSysStaffLanguageList(Map<String, Object> params) throws Exception;

	public int insertSysStaffLanguageMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysStaffLanguageMationToEdit(@Param("id") String id) throws Exception;

	public int editSysStaffLanguageMationById(Map<String, Object> map) throws Exception;

	public int deleteSysStaffLanguageMationById(@Param("id") String id) throws Exception;

	public List<Map<String, Object>> queryPointStaffSysStaffLanguageList(Map<String, Object> params) throws Exception;

}
