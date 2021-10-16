/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MQUserEmailDao {
	
	public List<Map<String, Object>> queryEmailListByEmailAddress(Map<String, Object> map) throws Exception;

	public int insertEmailListToServer(List<Map<String, Object>> enclosureBeans) throws Exception;
	
	public int insertEmailEnclosureListToServer(List<Map<String, Object>> beans) throws Exception;
	
	public List<Map<String, Object>> queryEmailListByEmailFromAddress(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryDeleteEmailListByEmailFromAddress(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryDraftsEmailListByEmailFromAddress(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryEmailMessageIdByEmailId(Map<String, Object> map) throws Exception;
	
	public int editEmailMessageIdByEmailId(Map<String, Object> emailEditMessageId) throws Exception;

	public Map<String, Object> queryServiceMationBySericeId(@Param("serviceId") String serviceId) throws Exception;

	public List<Map<String, Object>> queryCooperationUserNameById(@Param("serviceId") String serviceId) throws Exception;

	public int insertNoticeListMation(List<Map<String, Object>> notices) throws Exception;
	
}
