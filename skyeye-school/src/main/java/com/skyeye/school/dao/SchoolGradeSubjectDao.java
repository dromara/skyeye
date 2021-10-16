/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SchoolGradeSubjectDao
 * @Description: 年级与技能的绑定关系管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 20:47
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SchoolGradeSubjectDao {

	public List<Map<String, Object>> querySchoolGradeSubjectList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolGradeSkillById(@Param("gradeId") String gradeId) throws Exception;

	public Map<String, Object> querySchoolGradeMationById(Map<String, Object> map) throws Exception;

	public int deleteSchoolGradeSubjectMation(@Param("gradeId") String gradeId) throws Exception;

	public int insertSchoolGradeSubjectMation(List<Map<String, Object>> beans) throws Exception;

}
