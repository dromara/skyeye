/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SealServicePhoneDao {

	public List<Map<String, Object>> queryNumberInEveryStateIsMine(@Param("userId") String userId) throws Exception;

	public List<Map<String, Object>> queryNumberInEveryStateIsAll() throws Exception;

	public Map<String, Object> querySealSeServiceState(@Param("id") String string) throws Exception;

	public int insertSealSeServiceWaitToSignonMation(Map<String, Object> map) throws Exception;

	public int editSealSeServiceWaitToSignonMation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryFeedBackList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllPartsList(Map<String, Object> params) throws Exception;

}
