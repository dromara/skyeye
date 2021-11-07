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

	/**
	 * 分页+typeName模糊查询动态表单页面分类列表
	 *
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> queryDsFormPageTypeList(Map<String, Object> map);

	/**
	 * 新增动态表单页面分类
	 *
	 * @param map
	 */
	void insertDsFormPageType(Map<String, Object> map);

	/**
	 * 根据id查询动态表单页面分类详情
	 *
	 * @param id 唯一标识
	 * @return 详情信息
	 */
	Map<String, Object> queryDsFormPageTypeById(@Param("id") String id);

	/**
	 * 根据parentId+typeName查询动态表单页面的id
	 * @param map
	 * @return 唯一标识id
	 */
	String queryDsFormPageTypeByParentIdAndTypeName(Map<String, Object> map);

	/**
	 * 根据parentId查询动态表单页面分类列表
	 *
	 * @param parentId 父节点id
	 * @return 动态表单页面分类列表
	 */
	List<Map<String, Object>> queryDsFormPageTypeByParentId(@Param("parentId") String parentId);

	/**
	 * 根据id更新动态表单页面分类
	 *
	 * @param map
	 * @throws Exception
	 */
	void updateDsFormPageTypeById(Map<String, Object> map);

	/**
	 * 根据id删除动态表单页面分类
	 *
	 * @param id 唯一标识
	 */
	void delDsFormPageTypeById(@Param("id") String id);

	/**
	 * 根据parentid删除动态表单页面分类
	 *
	 * @param parentId 父节点id
	 */
	void delDsFormPageTypeByParentId(@Param("parentId") String parentId);

}
