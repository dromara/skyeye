/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SchoolTeacherSubjectDao {

	public List<Map<String, Object>> querySchoolTeacherSubjectList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolTeacherSkillById(@Param("staffId") String staffId) throws Exception;

	public Map<String, Object> querySchoolTeacherMationById(Map<String, Object> map) throws Exception;

	public int deleteSchoolTeacherSubjectMation(@Param("staffId") String staffId) throws Exception;

	public int insertSchoolTeacherSubjectMation(List<Map<String, Object>> beans) throws Exception;

}
