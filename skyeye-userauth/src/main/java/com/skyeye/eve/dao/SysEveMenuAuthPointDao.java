/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SysEveMenuAuthPointDao {

	public List<Map<String, Object>> querySysEveMenuAuthPointListByMenuId(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveMenuAuthPointMationByAuthName(Map<String, Object> map) throws Exception;

	public int insertSysEveMenuAuthPointMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveMenuAuthPointMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveMenuAuthPointMationByAuthNameAndId(Map<String, Object> map) throws Exception;

	public int editSysEveMenuAuthPointMationById(Map<String, Object> map) throws Exception;

	public int deleteSysEveMenuAuthPointMationById(Map<String, Object> map) throws Exception;
	
}
