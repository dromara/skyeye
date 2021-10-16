/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

public interface ForumTagDao {

	public List<Map<String, Object>> queryForumTagList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryForumTagMationByName(Map<String, Object> map) throws Exception;

	public int insertForumTagMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryForumTagBySimpleLevel(Map<String, Object> map) throws Exception;

	public int deleteForumTagById(Map<String, Object> map) throws Exception;

	public int updateUpForumTagById(Map<String, Object> map) throws Exception;

	public int updateDownForumTagById(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectForumTagById(Map<String, Object> map) throws Exception;

	public int editForumTagMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryForumTagUpMationById(Map<String, Object> map) throws Exception;

	public int editForumTagMationOrderNumUpById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryForumTagDownMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryForumTagStateById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryForumTagUpStateList(Map<String, Object> map) throws Exception;

}
