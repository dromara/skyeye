/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProProjectDiscussDao
 * @Description: 项目讨论板管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:28
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProProjectDiscussDao {

	public List<Map<String, Object>> queryProProjectDiscussList(Map<String, Object> map) throws Exception;

	public int insertProProjectDiscuss(Map<String, Object> map) throws Exception;

	public int insertProProjectDiscussReply(Map<String, Object> map) throws Exception;

	public int deleteDiscussMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryDiscussMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDiscussReplyByDiscussId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryAllDiscussList(Map<String, Object> map) throws Exception;



}
