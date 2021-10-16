/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import java.util.List;
import java.util.Map;

public interface SchoolBodyMindDao {

	public List<Map<String, Object>> querySchoolBodyMindList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolBodyMindByName(Map<String, Object> map) throws Exception;

	public int insertSchoolBodyMindMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolBodyMindToEditById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolBodyMindByNameAndId(Map<String, Object> map) throws Exception;

	public int editSchoolBodyMindById(Map<String, Object> map) throws Exception;

	public int deleteSchoolBodyMindById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolBodyMindListToShow(Map<String, Object> map) throws Exception;

}
