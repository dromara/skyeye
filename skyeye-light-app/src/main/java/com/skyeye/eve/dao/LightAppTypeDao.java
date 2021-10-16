/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface LightAppTypeDao {

	public List<Map<String, Object>> queryLightAppTypeList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryLightAppTypeMationByTypeName(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryLightAppTypeMationByTypeNameAndId(Map<String, Object> map) throws Exception;

	public int insertLightAppTypeMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryLightAppTypeAfterOrderBum(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryLightAppTypeMationToEditById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryLightAppTypeMationStateById(Map<String, Object> map) throws Exception;
	
	public int editLightAppTypeMationById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryLightAppTypeISTopByThisId(Map<String, Object> map) throws Exception;

	public int editLightAppTypeSortTopById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryLightAppTypeISLowerByThisId(Map<String, Object> map) throws Exception;

	public int editLightAppTypeSortLowerById(Map<String, Object> map) throws Exception;
	
	public int deleteLightAppTypeById(Map<String, Object> map) throws Exception;
	
	public int editLightAppTypeUpTypeById(Map<String, Object> map) throws Exception;
	
	public int editLightAppTypeDownTypeById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryLightAppTypeUpList(Map<String, Object> map) throws Exception;

}
