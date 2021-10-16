/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SysDataSqlDao {

	public List<Map<String, Object>> querySysDataSqlBackupsList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllTableMationList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryDataSqlVersion(Map<String, Object> map) throws Exception;

	public int insertTableBackUps(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDataSqlVersionById(Map<String, Object> map) throws Exception;

}
