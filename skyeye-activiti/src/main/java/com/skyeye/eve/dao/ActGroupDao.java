/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface ActGroupDao {

	int insertActGroupMation(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> selectAllActGroupMation(Map<String, Object> map) throws Exception;

	int editActGroupNameByGroupId(Map<String, Object> map) throws Exception;

	int deleteActGroupByGroupId(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> queryGroupListToActiviti(Map<String, Object> parmter) throws Exception;

	List<Map<String, Object>> queryActGroupList(Map<String, Object> map) throws Exception;

}
