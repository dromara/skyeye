/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.Map;

/**
 *
 * @ClassName: ApiMationDao
 * @Description: api接口信息数据层
 * @author: skyeye云系列
 * @date: 2021/11/20 13:03
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ApiMationDao {

	public Map<String, Object> queryApiMationByRequestUrl(Map<String, Object> map) throws Exception;

	public int insertApiMationMation(Map<String, Object> map) throws Exception;

	public Map<String, Object> selectApiMationById(Map<String, Object> map) throws Exception;

	public int editApiMationById(Map<String, Object> map) throws Exception;

	}
