/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysStaffJobResumeDao {

	public List<Map<String, Object>> queryAllSysStaffJobResumeList(Map<String, Object> params) throws Exception;

	public int insertSysStaffJobResumeMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysStaffJobResumeMationToEdit(@Param("id") String id) throws Exception;

	public int editSysStaffJobResumeMationById(Map<String, Object> map) throws Exception;

	public int deleteSysStaffJobResumeMationById(@Param("id") String id) throws Exception;

	public List<Map<String, Object>> queryPointStaffSysStaffJobResumeList(Map<String, Object> params) throws Exception;

}
