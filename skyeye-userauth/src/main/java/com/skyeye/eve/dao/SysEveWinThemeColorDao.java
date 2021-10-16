/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SysEveWinThemeColorDao {

	public List<Map<String, Object>> querySysEveWinThemeColorList(Map<String, Object> map) throws Exception;

	public int insertSysEveWinThemeColorMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveWinThemeColorMationByName(Map<String, Object> map) throws Exception;

	public int deleteSysEveWinThemeColorMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveWinThemeColorMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveWinThemeColorMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editSysEveWinThemeColorMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysEveWinThemeColorListToShow(Map<String, Object> map) throws Exception;

}
