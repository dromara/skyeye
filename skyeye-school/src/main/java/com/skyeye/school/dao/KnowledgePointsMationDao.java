/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface KnowledgePointsMationDao {

	public List<Map<String, Object>> queryKnowledgePointsMationList(Map<String, Object> map) throws Exception;

	public int insertKnowledgePointsMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryKnowledgePointsMationByName(Map<String, Object> map) throws Exception;

	public int deleteKnowledgePointsMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryKnowledgePointsMationToEditById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryKnowledgePointsMationByNameAndId(Map<String, Object> map) throws Exception;

	public int editKnowledgePointsMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryKnowledgePointsMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryKnowledgePointsMationListToTable(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryKnowledgePointsMationListByIds(@Param("idsList") List<String> idsList) throws Exception;

	public List<Map<String, Object>> queryKnowledgePointsMationBankList(Map<String, Object> map) throws Exception;

}
