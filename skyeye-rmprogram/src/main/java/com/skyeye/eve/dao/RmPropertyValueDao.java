/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface RmPropertyValueDao {

	public List<Map<String, Object>> queryRmPropertyValueList(Map<String, Object> map) throws Exception;

	public int insertRmPropertyValueMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyValueMationByName(Map<String, Object> map) throws Exception;

	public int deleteRmPropertyValueMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyValueMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmPropertyValueMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editRmPropertyValueMationById(Map<String, Object> map) throws Exception;

}
