/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveModelTypeDao
 * @Description: 系统模板分类数据层
 * @author: skyeye云系列
 * @date: 2021/11/13 10:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveModelTypeDao {

	/**
	 * 分页+typeName模糊查询系统模板分类列表
	 *
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> querySysEveModelTypeList(Map<String, Object> map);

	/**
	 * 新增系统模板分类
	 *
	 * @param map
	 */
	void insertSysEveModelType(Map<String, Object> map);

	/**
	 * 根据id查询系统模板分类详情
	 *
	 * @param id 唯一标识
	 * @return 详情信息
	 */
	Map<String, Object> querySysEveModelTypeById(@Param("id") String id);

	/**
	 * 根据parentId+typeName查询系统模板分类的id
	 * @param map
	 * @return 唯一标识id
	 */
	String querySysEveModelTypeByParentIdAndTypeName(Map<String, Object> map);

	/**
	 * 根据parentId查询系统模板分类列表
	 *
	 * @param parentId 父节点id
	 * @return 系统模板分类列表
	 */
	List<Map<String, Object>> querySysEveModelTypeByParentId(@Param("parentId") String parentId);

	/**
	 * 根据id更新系统模板分类
	 *
	 * @param map
	 * @throws Exception
	 */
	void updateSysEveModelTypeById(Map<String, Object> map);

	/**
	 * 根据id删除系统模板分类
	 *
	 * @param id 唯一标识
	 */
	void delSysEveModelTypeById(@Param("id") String id);

	/**
	 * 根据parentid删除系统模板分类
	 *
	 * @param parentId 父节点id
	 */
	void delSysEveModelTypeByParentId(@Param("parentId") String parentId);

}
