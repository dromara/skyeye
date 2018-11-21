package com.skyeye.authority.dao;

import java.util.List;
import java.util.Map;

public interface SysMonitorDao {

	public int insertMonitorMation(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> queryMonitorInfoMationList(Map<String, Object> map) throws Exception;

	public int deleteMonitorSaveFiveHandlber(Map<String, Object> bean) throws Exception;

}
