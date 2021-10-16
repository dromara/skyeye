/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface LightAppDao {

	public List<Map<String, Object>> queryLightAppList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryLightAppMationByName(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryLightAppMationByNameAndId(Map<String, Object> map) throws Exception;

	public int insertLightAppMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryLightAppMationToEditById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryLightAppStateById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryLightAppMationToAddWinById(Map<String, Object> map) throws Exception;
	
	public int editLightAppMationById(Map<String, Object> map) throws Exception;
	
	public int deleteLightAppById(Map<String, Object> map) throws Exception;
	
	public int editLightAppUpById(Map<String, Object> map) throws Exception;
	
	public int editLightAppDownById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryLightAppUpList(Map<String, Object> map) throws Exception;

}
