/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ApiGroupDao
 * @Description: api接口分组数据层
 * @author: skyeye云系列
 * @date: 2021/11/28 13:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ApiGroupDao {

	public List<Map<String, Object>> queryApiGroupList(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryApiGroupByName(Map<String, Object> map) throws Exception;

	public int insertApiGroup(List<Map<String, Object>> beans) throws Exception;

	public int deleteApiGroupById(Map<String, Object> map) throws Exception;

	public int deleteApiGroupByModelId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryApiGroupToEditById(Map<String, Object> map) throws Exception;

	public int editApiGroupById(Map<String, Object> map) throws Exception;

}
