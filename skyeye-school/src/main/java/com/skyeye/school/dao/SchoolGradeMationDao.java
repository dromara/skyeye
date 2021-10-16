/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SchoolGradeMationDao {

	public List<Map<String, Object>> querySchoolGradeMationList(Map<String, Object> map) throws Exception;

	public int insertSchoolGradeMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolGradeMationByName(Map<String, Object> map) throws Exception;

	public int deleteSchoolGradeMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolGradeMationToEditById(Map<String, Object> map) throws Exception;

	public int editSchoolGradeMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllGradeMationBySchoolId(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolGradeOrderByNumber(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryGradeUpMationById(Map<String, Object> map) throws Exception;

	public int editGradeMationOrderNumToUp(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryGradeDownMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryGradeNowYearMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryClassMationList(@Param("gradeId") String gradeId, @Param("year") String year);

}
