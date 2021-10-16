/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SysEveDesktopDao {

	public List<Map<String, Object>> querySysDesktopList(Map<String, Object> map) throws Exception;

	public int insertSysDesktopMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDesktopBySimpleLevel(Map<String, Object> map) throws Exception;

	public int deleteSysDesktopById(Map<String, Object> map) throws Exception;

	public int updateUpSysDesktopById(Map<String, Object> map) throws Exception;

	public int updateDownSysDesktopById(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectSysDesktopById(Map<String, Object> map) throws Exception;

	public int editSysDesktopMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDesktopUpMationById(Map<String, Object> map) throws Exception;

	public int editSysDesktopMationOrderNumUpById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDesktopDownMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDesktopStateById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDesktopByDesktopName(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryAllSysDesktopList(Map<String, Object> map) throws Exception;
	
	public int removeAllSysEveMenuByDesktopId(Map<String, Object> map) throws Exception;
	
    public Map<String, Object> querySysDesktopStateAndMenuNumById(Map<String, Object> map) throws Exception;

}
