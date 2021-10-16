/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface ReportTypeDao {

	public List<Map<String, Object>> queryReportTypeList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryReportTypeMationByTypeName(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryReportTypeMationByTypeNameAndId(Map<String, Object> map) throws Exception;

	public int insertReportTypeMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryReportTypeAfterOrderBum(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryReportTypeMationToEditById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryReportTypeMationStateById(Map<String, Object> map) throws Exception;
	
	public int editReportTypeMationById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryReportTypeISTopByThisId(Map<String, Object> map) throws Exception;

	public int editReportTypeSortTopById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryReportTypeISLowerByThisId(Map<String, Object> map) throws Exception;

	public int editReportTypeSortLowerById(Map<String, Object> map) throws Exception;
	
	public int deleteReportTypeById(Map<String, Object> map) throws Exception;
	
	public int editReportTypeUpTypeById(Map<String, Object> map) throws Exception;
	
	public int editReportTypeDownTypeById(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryReportTypeUpList(Map<String, Object> map) throws Exception;
	
}
