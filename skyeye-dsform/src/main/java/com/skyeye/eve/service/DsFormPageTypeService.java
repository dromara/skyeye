/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: DsFormPageTypeService
 * @Description: 动态表单页面分类业务层
 * @author: skyeye云系列--郑杰
 * @date: 2021/11/6 23:33
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DsFormPageTypeService {

	/**
	 * 获取动态表单页面分类列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	void queryDsFormPageTypeList(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 新增动态表单页面分类
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	void insertDsFormPageType(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 根据id查询动态表单页面分类详情
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	void queryDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 通过parentId查找对应的动态表单分类列表
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	void queryDsFormPageTypeByParentId(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 通过id编辑对应的动态表单分类信息
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	void updateDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 删除动态表单分类
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	void delDsFormPageTypeById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
