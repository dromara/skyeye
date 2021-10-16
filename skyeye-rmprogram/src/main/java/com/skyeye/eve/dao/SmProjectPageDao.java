/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SmProjectPageDao {

	public List<Map<String, Object>> queryProPageMationByProIdList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryFilePathNumMaxMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryFileNameNumMaxMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySortMaxMationByProjectId(Map<String, Object> map) throws Exception;

	public int insertProPageMationByProId(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectPageISTopByThisId(Map<String, Object> map) throws Exception;

	public int editSmProjectPageSortTopById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectPageISLowerByThisId(Map<String, Object> map) throws Exception;

	public int editSmProjectPageSortLowerById(Map<String, Object> topBean) throws Exception;

	public Map<String, Object> querySmProjectPageMationToEditById(Map<String, Object> map) throws Exception;

	public int editSmProjectPageMationById(Map<String, Object> map) throws Exception;

	public int deleteSmProjectPageMationById(Map<String, Object> map) throws Exception;

	public int deleteSmProjectPageModeMationById(Map<String, Object> map) throws Exception;

}
