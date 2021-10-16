/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface DsFormContentDao {

	public List<Map<String, Object>> queryDsFormContentList(Map<String, Object> map) throws Exception;

	public int insertDsFormContentMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormContentMationByName(Map<String, Object> map) throws Exception;

	public int deleteDsFormContentMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormContentMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormContentMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editDsFormContentMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDsFormContentMationToShow(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDsFormContentDetailedMationToShow(Map<String, Object> map) throws Exception;

}
