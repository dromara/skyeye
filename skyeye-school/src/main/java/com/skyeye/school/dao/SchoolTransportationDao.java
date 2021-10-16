/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import java.util.List;
import java.util.Map;

public interface SchoolTransportationDao {

	public List<Map<String, Object>> querySchoolTransportationList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolTransportationByName(Map<String, Object> map) throws Exception;

	public int insertSchoolTransportationMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolTransportationToEditById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolTransportationByNameAndId(Map<String, Object> map) throws Exception;

	public int editSchoolTransportationById(Map<String, Object> map) throws Exception;

	public int deleteSchoolTransportationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolTransportationListToShow(Map<String, Object> map) throws Exception;

}
