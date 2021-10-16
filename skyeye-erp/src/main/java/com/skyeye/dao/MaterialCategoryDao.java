/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface MaterialCategoryDao {

	public List<Map<String, Object>> queryMaterialCategoryList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryMaterialCategoryMationByName(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryMaterialCategoryMationByNameAndId(Map<String, Object> map) throws Exception;

	public int insertMaterialCategoryMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialCategoryBySimpleLevel(Map<String, Object> map) throws Exception;

	public int deleteMaterialCategoryById(Map<String, Object> map) throws Exception;

	public int deleteMaterialCategoryByParentId(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectMaterialCategoryToEditById(Map<String, Object> map) throws Exception;

	public int editMaterialCategoryMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialCategoryUpMationById(Map<String, Object> map) throws Exception;

	public int editMaterialCategoryMationOrderNumById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialCategoryDownMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaterialCategoryStateById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryMaterialCategoryListToTree() throws Exception;

}
