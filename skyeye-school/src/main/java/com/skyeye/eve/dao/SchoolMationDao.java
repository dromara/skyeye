/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SchoolMationDao {

	public List<Map<String, Object>> querySchoolMationList(Map<String, Object> map) throws Exception;

	public int insertSchoolMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolMationByName(Map<String, Object> map) throws Exception;

	public int deleteSchoolMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editSchoolMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryOverAllSchoolMationList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolListToSelect(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolPowerListToSelect(Map<String, Object> map) throws Exception;

}
