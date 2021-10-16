/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SysTAreaDao {

	public List<Map<String, Object>> querySysTAreaList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaProvinceList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaCityList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaChildAreaList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysTAreaTownShipList(Map<String, Object> map) throws Exception;

}
