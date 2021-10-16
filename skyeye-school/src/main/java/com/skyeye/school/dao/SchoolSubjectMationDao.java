/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import java.util.List;
import java.util.Map;

public interface SchoolSubjectMationDao {

	public List<Map<String, Object>> querySchoolSubjectMationList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolSubjectMationByName(Map<String, Object> map) throws Exception;

	public int insertSchoolSubjectMationMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolSubjectMationToEditById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolSubjectMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editSchoolSubjectMationById(Map<String, Object> map) throws Exception;

	public int deleteSchoolSubjectMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolSubjectMationListToShow(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolSubjectMationListToShowByGradeId(Map<String, Object> map) throws Exception;

}
