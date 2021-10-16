/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SchoolFloorMationDao
 * @Description: 教学楼管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 20:48
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SchoolFloorMationDao {

	public List<Map<String, Object>> querySchoolFloorMationList(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolFloorMationByName(Map<String, Object> map) throws Exception;

	public int insertSchoolFloorMationMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySchoolFloorMationToEditById(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> querySchoolFloorMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editSchoolFloorMationById(Map<String, Object> map) throws Exception;

	public int deleteSchoolFloorMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySchoolFloorMationToSelectList(Map<String, Object> map) throws Exception;

}
