/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface CheckWorkReasonDao {

	public List<Map<String, Object>> queryCheckWorkReasonList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCheckWorkReasonByName(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCheckWorkReasonBySimpleLevel(Map<String, Object> map) throws Exception;

	public int insertCheckWorkReasonMation(Map<String, Object> map) throws Exception;

	public int deleteCheckWorkReasonById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCheckWorkReasonStateById(Map<String, Object> map) throws Exception;

	public int updateUpCheckWorkReasonById(Map<String, Object> map) throws Exception;

	public int updateDownCheckWorkReasonById(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectCheckWorkReasonById(Map<String, Object> map) throws Exception;

	public int editCheckWorkReasonMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCheckWorkReasonUpMationById(Map<String, Object> map) throws Exception;

	public int editCheckWorkReasonMationOrderNumUpById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCheckWorkReasonDownMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCheckWorkReasonUpStateList(Map<String, Object> map) throws Exception;

}
