/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SmProjectPageModeDao {

	public List<Map<String, Object>> queryProPageModeMationByPageIdList(Map<String, Object> map) throws Exception;

	public int deletePageModelMationListByPageId(Map<String, Object> map) throws Exception;

	public int editProPageModeMationByPageIdList(List<Map<String, Object>> beans) throws Exception;

	public List<Map<String, Object>> queryPropertyListByMemberId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryPropertyValuesListByPropertyId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryProPageMationByPageId(Map<String, Object> map) throws Exception;

}
