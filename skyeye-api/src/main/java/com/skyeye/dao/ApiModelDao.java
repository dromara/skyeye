/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ApiModelDao
 * @Description: api接口模块数据层
 * @author: skyeye云系列
 * @date: 2021/11/27 16:12
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ApiModelDao {

    public List<Map<String, Object>> queryApiModelList(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryApiModelMationByName(Map<String, Object> map) throws Exception;

    public int insertApiModel(List<Map<String, Object>> beans) throws Exception;

    public int deleteApiModelById(Map<String, Object> map) throws Exception;

    public Map<String, Object> queryApiModelToEditById(Map<String, Object> map) throws Exception;

    public int editApiModelById(Map<String, Object> map) throws Exception;

}
