/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SchoolTimeSettingDao {

	public List<Map<String, Object>> queryGradeListToRow(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySemesterListToCol(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryTimeSettingList(Map<String, Object> map) throws Exception;

	public int deleteSchoolTimeSettingMation(@Param("schoolId") String schoolId) throws Exception;

	public int insertSchoolTimeSettingMation(List<Map<String, Object>> entitys) throws Exception;

}
