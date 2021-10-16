/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SysNoticeTypeDao {

	public List<Map<String, Object>> querySysNoticeTypeList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeTypeMationByName(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySysNoticeTypeMationByNameAndId(Map<String, Object> map) throws Exception;

	public int insertSysNoticeTypeMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeTypeBySimpleLevel(Map<String, Object> map) throws Exception;

	public int deleteSysNoticeTypeById(Map<String, Object> map) throws Exception;

	public int updateUpSysNoticeTypeById(Map<String, Object> map) throws Exception;

	public int updateDownSysNoticeTypeById(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectSysNoticeTypeById(Map<String, Object> map) throws Exception;

	public int editSysNoticeTypeMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeTypeUpMationById(Map<String, Object> map) throws Exception;

	public int editSysNoticeTypeMationOrderNumUpById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeTypeDownMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeTypeStateById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryFirstSysNoticeTypeUpStateList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllFirstSysNoticeTypeStateList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySecondSysNoticeTypeUpStateList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysNoticeTypeById(Map<String, Object> map) throws Exception;

}
