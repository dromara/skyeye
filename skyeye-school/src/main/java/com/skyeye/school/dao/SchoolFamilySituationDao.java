/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import java.util.List;
import java.util.Map;

public interface SchoolFamilySituationDao {

	public List<Map<String, Object>> querySchoolFamilySituationList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolFamilySituationByName(Map<String, Object> map) throws Exception;

	public int insertSchoolFamilySituationMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolFamilySituationToEditById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolFamilySituationByNameAndId(Map<String, Object> map) throws Exception;

	public int editSchoolFamilySituationById(Map<String, Object> map) throws Exception;

	public int deleteSchoolFamilySituationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolFamilySituationListToShow(Map<String, Object> map) throws Exception;

}
