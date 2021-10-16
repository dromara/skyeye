/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface ActGroupDao {

	public int insertActGroupMation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> selectAllActGroupMation(Map<String, Object> map) throws Exception;

	public int insertActGroupUserByGroupId(List<Map<String, Object>> beans) throws Exception;

	public int editActGroupNameByGroupId(Map<String, Object> map) throws Exception;

	public int deleteActGroupByGroupId(Map<String, Object> map) throws Exception;

	public int deleteActGroupUserByGroupId(Map<String, Object> map) throws Exception;

	public int deleteActGroupUserByGroupIdAndUserId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> selectUserInfoOnActGroup(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUserIsInActGroup(Map<String, Object> map) throws Exception;

}
