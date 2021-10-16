/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface RmTypeDao {

	public List<Map<String, Object>> queryRmTypeList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmTypeByName(Map<String, Object> map) throws Exception;

	public int insertRmTypeMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmTypeGroupNumById(Map<String, Object> map) throws Exception;

	public int deleteRmTypeById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmTypeMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmTypeMationByIdAndName(Map<String, Object> map) throws Exception;

	public int editRmTypeMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmTypeISTop(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmTypeISTopByThisId(Map<String, Object> map) throws Exception;

	public int editRmTypeSortTopById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryRmTypeISLowerByThisId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryRmTypeAllList(Map<String, Object> map) throws Exception;

}
