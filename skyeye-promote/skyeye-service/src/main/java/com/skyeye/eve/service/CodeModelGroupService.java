/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 代码生成器模板分组服务接口类
 *
 * @auther 卫志强 QQ：598748873@qq.com，微信：wzq_598748873
 * @desc 禁止商用
 */
public interface CodeModelGroupService {

	public void queryCodeModelGroupList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertCodeModelGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteCodeModelGroupById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCodeModelGroupMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editCodeModelGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryTableParameterByTableName(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryTableMationByTableName(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryCodeModelListByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception;

}
