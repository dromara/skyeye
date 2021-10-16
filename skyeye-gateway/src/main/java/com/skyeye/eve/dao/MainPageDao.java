/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface MainPageDao {

	public String queryCheckOnWorkNumByUserId(@Param("userId") String userId) throws Exception;

	public String queryDiskCloudFileNumByUserId(@Param("userId") String userId) throws Exception;

	public String queryForumNumByUserId(@Param("userId") String userId) throws Exception;

	public String queryKnowledgeNumByUserId(@Param("userId") String userId) throws Exception;

	public List<Map<String, Object>> queryFirstSysNoticeTypeUpStateList() throws Exception;

	public List<Map<String, Object>> queryNoticeContentListByUserIdAndTypeId(@Param("userId") String userId, @Param("typeId") String typeId) throws Exception;

	public List<Map<String, Object>> queryHotForumList(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryKnowledgeContentPhoneList(Map<String, Object> map) throws Exception;

}
