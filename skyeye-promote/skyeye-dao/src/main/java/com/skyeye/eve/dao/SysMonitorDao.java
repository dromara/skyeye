/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface SysMonitorDao {

	public int insertMonitorMation(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> queryMonitorInfoMationList(Map<String, Object> map) throws Exception;

	public int deleteMonitorSaveFiveHandlber(Map<String, Object> bean) throws Exception;

}
