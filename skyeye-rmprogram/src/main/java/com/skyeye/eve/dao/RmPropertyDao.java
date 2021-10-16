/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface RmPropertyDao {

	public List<Map<String, Object>> queryRmPropertyList(Map<String, Object> map) throws Exception;

	public int insertRmPropertyMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyMationByName(Map<String, Object> map) throws Exception;

	public int deleteRmPropertyMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editRmPropertyMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryRmPropertyListToShow(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyValueNumById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUseRmPropertyNumById(Map<String, Object> map) throws Exception;

}
