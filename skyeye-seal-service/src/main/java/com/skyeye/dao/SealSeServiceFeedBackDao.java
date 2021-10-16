/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

public interface SealSeServiceFeedBackDao {

	public List<Map<String, Object>> queryFeedBackList(Map<String, Object> map) throws Exception;

	public int insertFeedBackMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySealServiceMationToFeedBack(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryFeedBackMationToEditById(Map<String, Object> map) throws Exception;

	public int editFeedBackMationById(Map<String, Object> map) throws Exception;

	public int deleteFeedBackMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryFeedBackDetailsMationById(Map<String, Object> map) throws Exception;

}
