/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SchoolClassMationDao
 * @Description: 班级信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:21
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SchoolClassMationDao {

	public List<Map<String, Object>> querySchoolClassMationList(Map<String, Object> map) throws Exception;

	public int insertSchoolClassMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolClassMationByName(Map<String, Object> map) throws Exception;

	public int deleteSchoolClassMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolClassMationToEditById(Map<String, Object> map) throws Exception;

	public int editSchoolClassMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryNewClassGradeMationByGradeId(Map<String, Object> map) throws Exception;

	public int editClassGradeMationToHistoryByGradeId(@Param("gradeId") String gradeId) throws Exception;

}
