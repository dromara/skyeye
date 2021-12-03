/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ApiPropertyDao
 * @Description: api接口参数数据层
 * @author: skyeye云系列
 * @date: 2021/11/28 14:42
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ApiPropertyDao {

    public List<Map<String, Object>> queryApiPropertyList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryApiPropertyMationByApiId(Map<String, Object> map) throws Exception;

    public int insertApiProperty(List<Map<String, Object>> beans) throws Exception;

    public int deleteApiPropertyById(Map<String, Object> map) throws Exception;

    public int deleteApiPropertyByApiId(Map<String, Object> map) throws Exception;

    public int deleteApiPropertyByGroupId(Map<String, Object> map) throws Exception;

    public int deleteApiPropertyByModelId(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryApiPropertyToEditById(Map<String, Object> map) throws Exception;

    public int editApiPropertyById(Map<String, Object> map) throws Exception;

}
