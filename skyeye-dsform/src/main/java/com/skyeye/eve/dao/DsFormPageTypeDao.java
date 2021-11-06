/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DsFormPageTypeDao
 * @Description: 动态表单页面分类数据层
 * @author: skyeye云系列--郑杰
 * @date: 2021/11/6 23:33
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DsFormPageTypeDao {

	List<Map<String, Object>> queryDsFormPageTypeList(Map<String, Object> map);

	void insertDsFormPageType(Map<String, Object> map);

	Map<String, Object> queryDsFormPageTypeById(@Param("id") String id);

	String queryDsFormPageTypeByParentIdAndTypeName(Map<String, Object> map);

	List<Map<String, Object>> queryDsFormPageTypeByParentId(@Param("parentId") String parentId);

	void updateDsFormPageTypeById(Map<String, Object> map) throws Exception;

	void delDsFormPageTypeById(@Param("id") String id) throws Exception;

}
