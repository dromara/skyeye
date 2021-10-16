/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SchoolStudentMationDao {

	public List<Map<String, Object>> querySchoolStudentMationList(Map<String, Object> map) throws Exception;

	public int insertSchoolStudentMation(Map<String, Object> map) throws Exception;

	public int insertSchoolStudentParentsMation(List<Map<String, Object>> entitys) throws Exception;

	public int insertSchoolStudentHomeSituationMation(List<Map<String, Object>> beans) throws Exception;

	public int insertSchoolStudentBodyMindMation(List<Map<String, Object>> items) throws Exception;

	public Map<String, Object> queryStudentMationByIdCardAndSruNo(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryNotDividedIntoClassesSchoolStudentMationList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolStudentMationToOperatorById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolStudentAssignmentClassMationById(Map<String, Object> map) throws Exception;

	public int editAssignmentClassByStuId(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolStudentMationToEditById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolStudentParentsMationToEditById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolStudentHomeSituationMationToEditById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolStudentBodyMindMationToEditById(Map<String, Object> map) throws Exception;

	public int editSchoolStudentMationById(Map<String, Object> map) throws Exception;

	public int deleteSchoolStudentParentsMationByStuId(@Param("studentId") String studentId) throws Exception;

	public int deleteSchoolStudentHomeSituationMationByStuId(@Param("studentId") String studentId) throws Exception;

	public int deleteSchoolStudentBodyMindMationByStuId(@Param("studentId") String studentId) throws Exception;

	public Map<String, Object> querySchoolStudentMationDetailById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolStudentParentsMationDetailById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolStudentHomeSituationMationDetailById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolStudentBodyMindMationDetailById(Map<String, Object> map) throws Exception;

}
