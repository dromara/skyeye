/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SmProjectDao {

	public List<Map<String, Object>> querySmProjectList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectByNameAndUserId(Map<String, Object> map) throws Exception;

	public int insertSmProjectMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectPageNumById(Map<String, Object> map) throws Exception;

	public int deleteSmProjectById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectMationByIdAndName(Map<String, Object> map) throws Exception;

	public int editSmProjectMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySmProjectPageModelNumById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryGroupTypeMationList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryGroupMationListByTypeId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryGroupMemberMationList(Map<String, Object> map) throws Exception;


}
