/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface SysDeveLopDocDao {

	public List<Map<String, Object>> querySysDeveLopTypeList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopByName(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaxSysDeveLopBySimpleParentId(Map<String, Object> map) throws Exception;

	public int insertSysDeveLopType(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopTypeByIdToEdit(Map<String, Object> map) throws Exception;

	public int editSysDeveLopTypeById(Map<String, Object> map) throws Exception;

	public int deleteSysDeveLopTypeById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopTypeContentNumById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysDeveLopTypeByFirstType(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopByNameAndId(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopTypeStateById(Map<String, Object> map) throws Exception;

	public int editSysDeveLopTypeStateISupById(Map<String, Object> map) throws Exception;

	public int editSysDeveLopTypeStateISdownById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopTypeOrderByISupById(Map<String, Object> map) throws Exception;

	public int editSysDeveLopTypeOrderByISupById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopTypeOrderByISdownById(Map<String, Object> map) throws Exception;

	public int editSysDeveLopTypeOrderByISdownById(Map<String, Object> bean) throws Exception;

	public List<Map<String, Object>> querySysDeveLopDocList(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopDocByNameAndParentId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryMaxSysDeveLopDocBySimpleParentId(Map<String, Object> map) throws Exception;

	public int insertSysDeveLopDoc(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopDocByIdToEdit(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopDocByNameAndId(Map<String, Object> map) throws Exception;

	public int editSysDeveLopDocById(Map<String, Object> map) throws Exception;

	public int deleteSysDeveLopDocById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopDocStateById(Map<String, Object> map) throws Exception;

	public int editSysDeveLopDocStateISupById(Map<String, Object> map) throws Exception;

	public int editSysDeveLopDocStateISdownById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopDocOrderByISupById(Map<String, Object> map) throws Exception;

	public int editSysDeveLopDocOrderByISupById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopDocOrderByISdownById(Map<String, Object> map) throws Exception;

	public int editSysDeveLopDocOrderByISdownById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysDeveLopFirstTypeToShow(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysDeveLopSecondTypeToShow(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysDeveLopDocToShow(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysDeveLopDocContentToShow(Map<String, Object> map) throws Exception;

}
