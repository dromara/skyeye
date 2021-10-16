/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface MaterialUnitDao {

	public List<Map<String, Object>> queryMaterialUnitList(Map<String, Object> params) throws Exception;

	public Map<String, Object> queryUnitGroupMationByName(Map<String, Object> map) throws Exception;
	
	public int insertMaterialUnitGroupMation(Map<String, Object> map) throws Exception;

	public int insertMaterialUnitMation(List<Map<String, Object>> entitys) throws Exception;

	public int deleteMaterialUnitGroupMationById(Map<String, Object> map) throws Exception;

	public int deleteMaterialUnitMationByGroupId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialUnitGroupMationToEditById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMaterialUnitMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUnitGroupMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editMaterialUnitGroupMationById(Map<String, Object> bean) throws Exception;

	public int editUnitMationById(Map<String, Object> map) throws Exception;

	public int insertUnitMation(Map<String, Object> map) throws Exception;

	public int deleteUnitMationById(Map<String, Object> en) throws Exception;

	public List<Map<String, Object>> queryMaterialUnitListToSelect() throws Exception;

	public List<Map<String, Object>> queryMaterialUnitById(Map<String, Object> bean) throws Exception;

}
