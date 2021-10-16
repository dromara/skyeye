/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: KnowledgeContentDao
 * @Description: 企业知识库管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 18:51
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface KnowledgeContentDao {

	public int insertKnowledgeContentMation(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryKnowledgeContentList(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectKnowledgeContentById(Map<String, Object> map) throws Exception;

	public int editKnowledgeContentById(Map<String, Object> map) throws Exception;

	public int deleteKnowledgeContentById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryKnowledgeContentMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryUnCheckedKnowledgeContentList(Map<String, Object> map) throws Exception;

	public int editKnowledgeContentToCheck(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryKnowledgeContentByIdToCheck(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryCheckedKnowledgeContentList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryUncheckedKnowledgeContent(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryCheckedKnowledgeContent(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllPassKnowledgeContentList(Map<String, Object> map) throws Exception;
}
