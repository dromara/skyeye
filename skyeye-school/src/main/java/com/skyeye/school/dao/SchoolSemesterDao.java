/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import java.util.List;
import java.util.Map;

public interface SchoolSemesterDao {

	public List<Map<String, Object>> querySchoolSemesterList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolSemesterByName(Map<String, Object> map) throws Exception;

	public int insertSchoolSemesterMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolSemesterToEditById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolSemesterByNameAndId(Map<String, Object> map) throws Exception;

	public int editSchoolSemesterById(Map<String, Object> map) throws Exception;

	public int deleteSchoolSemesterById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolSemesterListToShow(Map<String, Object> map) throws Exception;

}
